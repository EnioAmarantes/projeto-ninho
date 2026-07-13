package io.projetoninho.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.library.view.OpenGlView
import io.projetoninho.client.BuildConfig
import io.projetoninho.client.core.video.CameraStreamManager

@Composable
fun CameraPreviewCard(
    isStreaming: Boolean,
    hasCameraPermission: Boolean,
    streamManagerRef: MutableState<CameraStreamManager?>,
    onStreamFailed: (String) -> Unit
) {
    if (!isStreaming) return

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
                                    onStreamSuccessCallback = {},
                                    onStreamFailedCallback = { error ->
                                        onStreamFailed(error)
                                    }
                                )
                                streamManagerRef.value = manager
                                manager.startPreview()
                                manager.startStream(BuildConfig.RTMP_URL)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                "Sem permissão de câmera",
                modifier = Modifier.align(Alignment.Center).padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
