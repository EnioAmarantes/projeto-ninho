package io.projetoninho.client.core.video

import com.pedro.common.ConnectChecker
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.library.rtmp.RtmpCamera2
import com.pedro.library.view.OpenGlView

class CameraStreamManager(
    private val openGlView: OpenGlView,
    private val onStreamSuccessCallback: () -> Unit = {},
    private val onStreamFailedCallback: (String) -> Unit = {}
) : ConnectChecker {

    private val rtmpCamera = RtmpCamera2(openGlView, this)

    fun startPreview() {
        try {
            if (!rtmpCamera.isOnPreview) {
                rtmpCamera.startPreview(CameraHelper.Facing.FRONT)
            }
        } catch (e: Exception) {
            onStreamFailedCallback("Erro ao iniciar preview: ${e.message}")
        }
    }

    fun stopPreview() {
        rtmpCamera.stopPreview()
    }

    fun startStream(url: String) {
        try {
            if (!rtmpCamera.isStreaming) {
                // Definindo resolução vertical (720x1280) para modo retrato nativo
                // Bitrate de 2Mbps para manter a qualidade no YOLO
                val videoPrepared = rtmpCamera.prepareVideo(720, 1280, 30, 2000 * 1024, 2, 90)
                val audioPrepared = rtmpCamera.prepareAudio(128 * 1024, 44100, true, true, true)

                if (videoPrepared && audioPrepared) {
                    android.util.Log.d("Stream", "Iniciando stream para: $url")
                    rtmpCamera.startStream(url)
                } else {
                    onStreamFailedCallback("Erro ao preparar hardware: Vídeo=$videoPrepared, Áudio=$audioPrepared")
                }
            }
        } catch (e: Exception) {
            onStreamFailedCallback("Exceção ao transmitir: ${e.message}")
        }
    }

    fun stopStream() {
        if (rtmpCamera.isStreaming) {
            rtmpCamera.stopStream()
        }
    }

    fun isStreaming() = rtmpCamera.isStreaming

    override fun onConnectionStarted(url: String) {}

    override fun onConnectionSuccess() {
        onStreamSuccessCallback()
    }

    override fun onConnectionFailed(reason: String) {
        // Removido o stopStream daqui para evitar recursão infinita se o erro persistir
        onStreamFailedCallback(reason)
    }

    override fun onNewBitrate(bitrate: Long) {}

    override fun onDisconnect() {}

    override fun onAuthError() {
        onConnectionFailed("Erro de autenticação")
    }

    override fun onAuthSuccess() {}
}
