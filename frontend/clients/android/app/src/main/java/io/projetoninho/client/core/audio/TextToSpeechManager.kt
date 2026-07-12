package io.projetoninho.client.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false

    var onStartSpeaking: () -> Unit = {}
    var onFinishedSpeaking: () -> Unit = {}

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Linguagem não suportada")
            } else {
                isInitialized = true
                setupProgressListener()
            }
        } else {
            Log.e("TTS", "Falha na inicialização")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onStartSpeaking()
            }

            override fun onDone(utteranceId: String?) {
                onFinishedSpeaking()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onFinishedSpeaking()
            }
        })
    }

    fun speak(text: String) {
        if (isInitialized) {
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "jarvis_response")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
