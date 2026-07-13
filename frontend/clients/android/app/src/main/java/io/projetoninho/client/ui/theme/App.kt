package io.projetoninho.client.ui.theme

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.projetoninho.client.BuildConfig
import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.data.ConversationRepositoryImpl
import io.projetoninho.client.conversation.presentation.ConversationViewModel
import io.projetoninho.client.core.audio.SpeechToTextManager
import io.projetoninho.client.core.audio.TextToSpeechManager
import io.projetoninho.client.core.network.ProjetoNinhoClient
import io.projetoninho.client.core.video.CameraStreamManager
import io.projetoninho.client.ui.components.*

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

    // Lógica de Permissões
    var hasMicPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasMicPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasMicPermission
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
    }

    // Lógica de Streaming
    var isStreaming by remember { mutableStateOf(false) }
    val streamManagerRef = remember { mutableStateOf<CameraStreamManager?>(null) }

    LaunchedEffect(isStreaming) {
        if (!isStreaming && streamManagerRef.value != null) {
            streamManagerRef.value?.stopStream()
            streamManagerRef.value?.stopPreview()
            streamManagerRef.value = null
        }
    }

    // Lógica de STT (Voz para Texto)
    val sttManager = remember {
        SpeechToTextManager(
            context = context,
            onSpeechResult = { text ->
                viewModel.onInputMessageChange(text)
                viewModel.sendMessage()
                viewModel.setListeningState(false)
            },
            onSpeechError = { viewModel.setListeningState(false) },
            onSpeechReady = { viewModel.setListeningState(true) },
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

    // Interface Principal (Limpa e Declarativa)
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        ProjetoNinhoHeader()

        Spacer(modifier = Modifier.height(16.dp))

        CameraPreviewCard(
            isStreaming = isStreaming,
            hasCameraPermission = hasCameraPermission,
            streamManagerRef = streamManagerRef,
            onStreamFailed = { error ->
                isStreaming = false
                viewModel.setErrorMessage("Erro no Stream: $error")
            }
        )

        OutlinedTextField(
            value = state.inputMessage,
            onValueChange = viewModel::onInputMessageChange,
            label = { Text("Digite sua mensagem") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && !state.isListening && !state.isSpeaking
        )

        Spacer(modifier = Modifier.height(8.dp))

        StreamButton(
            isStreaming = isStreaming,
            enabled = !state.isLoading && !state.isListening && !state.isSpeaking,
            onClick = {
                if (hasCameraPermission && hasMicPermission) {
                    isStreaming = !isStreaming
                } else {
                    permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                }
            }
        )

        if (isStreaming) {
            Text(
                text = "Transmitindo para: ${BuildConfig.RTMP_URL}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isListening) {
            VoiceWaveform()
        }

        SpeakButton(
            isListening = state.isListening,
            isSpeaking = state.isSpeaking,
            enabled = !state.isLoading && !state.isSpeaking && !state.isListening,
            onClick = {
                if (hasMicPermission) {
                    viewModel.setListeningState(true)
                    sttManager.startListening()
                } else {
                    permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SendButton(
            isLoading = state.isLoading,
            enabled = !state.isLoading && state.inputMessage.isNotBlank() && !state.isListening && !state.isSpeaking,
            onClick = viewModel::sendMessage
        )

        Spacer(modifier = Modifier.height(24.dp))

        JarvisResponseArea(
            errorMessage = state.errorMessage,
            responseMessage = state.responseMessage
        )

        Spacer(modifier = Modifier.weight(1f))

        ExitButton(onClick = { (context as? Activity)?.finish() })

        Spacer(modifier = Modifier.height(22.dp))
    }
}
