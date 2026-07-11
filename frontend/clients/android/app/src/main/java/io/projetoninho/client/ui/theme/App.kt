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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.projetoninho.client.conversation.presentation.ConversationViewModel

@Composable
fun App(
    viewModel: ConversationViewModel = viewModel()
) {
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
            value = viewModel.inputMessage,
            onValueChange = viewModel::onInputMessageChange,
            label = { Text("Digite sua mensagem") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = viewModel::sendMessage,
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading && viewModel.inputMessage.isNotBlank()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.responseMessage.isNotEmpty()) {
            Text(
                text = "Resposta:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = viewModel.responseMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
