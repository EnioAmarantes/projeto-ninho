package io.projetoninho.client.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.data.ConversationRepositoryImpl
import io.projetoninho.client.conversation.presentation.ConversationViewModel
import io.projetoninho.client.core.audio.TextToSpeechManager
import io.projetoninho.client.core.network.ProjetoNinhoClient

@Composable
fun App() {
    val context = LocalContext.current
    val repository = remember {
        ConversationRepositoryImpl(ConversationApi(ProjetoNinhoClient.http))
    }
    val ttsManager = remember {
        TextToSpeechManager(context)
    }
    val viewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModel.factory(repository, ttsManager)
    )
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
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = viewModel::sendMessage,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.inputMessage.isNotBlank()
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
