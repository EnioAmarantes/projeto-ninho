package io.projetoninho.client.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.data.ConversationRepositoryImpl
import io.projetoninho.client.conversation.presentation.ConversationViewModel
import io.projetoninho.client.core.audio.SpeechToTextManager
import io.projetoninho.client.core.audio.TextToSpeechManager
import io.projetoninho.client.core.network.ProjetoNinhoClient

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
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    val repository = remember {
        ConversationRepositoryImpl(ConversationApi(ProjetoNinhoClient.http))
    }

    val viewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModel.factory(
            repository,
            remember { TextToSpeechManager(context) }
        )
    )

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
            onSpeechEnd = {
                // Mantemos isListening até o onResult ou onError para bloquear o botão durante o processamento STT
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            sttManager.destroy()
        }
    }

    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Projeto Ninho",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.inputMessage,
            onValueChange = viewModel::onInputMessageChange,
            label = { Text("Digite sua mensagem") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && !state.isListening && !state.isSpeaking
        )

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
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
    }
}
