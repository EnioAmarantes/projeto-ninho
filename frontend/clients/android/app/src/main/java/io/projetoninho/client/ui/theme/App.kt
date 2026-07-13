package io.projetoninho.client.ui.theme

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.library.view.OpenGlView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.data.ConversationRepositoryImpl
import io.projetoninho.client.conversation.presentation.ConversationViewModel
import io.projetoninho.client.core.audio.SpeechToTextManager
import io.projetoninho.client.core.audio.TextToSpeechManager
import io.projetoninho.client.core.network.ProjetoNinhoClient
import io.projetoninho.client.core.video.CameraStreamManager

@Composable
fun VoiceWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animValues = List(5) { index ->
        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, delayMillis = index * 100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = Modifier
            .height(60.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animValues.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(anim.value.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
fun App() {
    val context = LocalContext.current
    
    val repository = remember {
        ConversationRepositoryImpl(ConversationApi(ProjetoNinhoClient.http))
    }

    val viewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModel.factory(
            repository,
            remember { TextToSpeechManager(context) }
        )
    )

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasMicPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasMicPermission
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
    }

    var isStreaming by remember { mutableStateOf(false) }
    
    // Usamos um holder estável para o manager fora da composição do AndroidView
    val streamManagerRef = remember { mutableStateOf<CameraStreamManager?>(null) }

    // Efeito para sincronizar o estado isStreaming com a ação de parar o stream
    LaunchedEffect(isStreaming) {
        if (!isStreaming && streamManagerRef.value != null) {
            streamManagerRef.value?.stopStream()
            streamManagerRef.value?.stopPreview()
            streamManagerRef.value = null
        }
    }

    val sttManager = remember {
        SpeechToTextManager(
            context = context,
            onSpeechResult = { text ->
                viewModel.onInputMessageChange(text)
                viewModel.sendMessage()
                viewModel.setListeningState(false)
            },
            onSpeechError = {
                viewModel.setListeningState(false)
            },
            onSpeechReady = {
                viewModel.setListeningState(true)
            },
            onSpeechEnd = { }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            sttManager.destroy()
            streamManagerRef.value?.stopStream()
            streamManagerRef.value?.stopPreview()
        }
    }

    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Projeto Ninho",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview da Câmera (só exibe se estiver streamando)
        if (isStreaming) {
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(4.dp)
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            OpenGlView(ctx).apply {
                                post {
                                    if (streamManagerRef.value == null) {
                                        val manager = CameraStreamManager(
                                            openGlView = this,
                                            onStreamSuccessCallback = { 
                                                // Já está true aqui pelo clique do botão
                                            },
                                            onStreamFailedCallback = { error ->
                                                isStreaming = false
                                                viewModel.setErrorMessage("Erro no Stream: $error")
                                            }
                                        )
                                        streamManagerRef.value = manager
                                        manager.startPreview()
                                        manager.startStream("rtmp://192.168.0.7:1935/live/camera")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        onRelease = {
                            // O stop é tratado pelo LaunchedEffect
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = state.inputMessage,
            onValueChange = viewModel::onInputMessageChange,
            label = { Text("Digite sua mensagem") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && !state.isListening && !state.isSpeaking
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botão de Stream
        Button(
            onClick = {
                if (hasCameraPermission && hasMicPermission) {
                    isStreaming = !isStreaming
                } else {
                    permissionsLauncher.launch(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isStreaming) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            ),
            enabled = !state.isLoading && !state.isListening && !state.isSpeaking
        ) {
            Icon(
                imageVector = if (isStreaming) Icons.Default.Stop else Icons.Default.Videocam,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isStreaming) "Encerrar Transmissão" else "Transmitir Câmera")
        }

        if (isStreaming) {
            Text(
                text = "Transmitindo para: rtmp://192.168.0.7:1935/live/camera",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isListening) {
            VoiceWaveform()
        }

        Button(
            onClick = {
                if (hasMicPermission) {
                    viewModel.setListeningState(true)
                    sttManager.startListening()
                } else {
                    permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && !state.isSpeaking && !state.isListening
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.isListening) {
                    Text("Ouvindo...")
                } else if (state.isSpeaking) {
                    Text("Jarvis falando...")
                } else {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Falar com Jarvis")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = viewModel::sendMessage,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.inputMessage.isNotBlank() && !state.isListening && !state.isSpeaking
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (state.responseMessage.isNotEmpty()) {
            Text(
                text = "Resposta:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = state.responseMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                (context as? Activity)?.finish()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Sair")
        }

        Spacer(modifier = Modifier.height(22.dp))
    }
}
