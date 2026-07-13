package io.projetoninho.client.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StreamButton(
    isStreaming: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isStreaming) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
        ),
        enabled = enabled
    ) {
        Icon(
            imageVector = if (isStreaming) Icons.Default.Stop else Icons.Default.Videocam,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(if (isStreaming) "Encerrar Transmissão" else "Transmitir Câmera")
    }
}

@Composable
fun SpeakButton(
    isListening: Boolean,
    isSpeaking: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isListening) {
                Text("Ouvindo...")
            } else if (isSpeaking) {
                Text("Jarvis falando...")
            } else {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Falar com Jarvis")
            }
        }
    }
}

@Composable
fun SendButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp).padding(end = 8.dp),
                strokeWidth = 2.dp
            )
        }
        Text("Enviar")
    }
}

@Composable
fun ExitButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Sair")
    }
}
