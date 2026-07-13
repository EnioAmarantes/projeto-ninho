package io.projetoninho.client.conversation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.projetoninho.client.conversation.data.ConversationRepository
import io.projetoninho.client.core.audio.TextToSpeechManager
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: ConversationRepository,
    private val ttsManager: TextToSpeechManager? = null
) : ViewModel() {

    var uiState by mutableStateOf(ConversationUiState())
        private set

    init {
        ttsManager?.onStartSpeaking = { setSpeakingState(true) }
        ttsManager?.onFinishedSpeaking = { setSpeakingState(false) }
    }

    fun setSpeakingState(isSpeaking: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isSpeaking = isSpeaking)
        }
    }

    fun setListeningState(isListening: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isListening = isListening)
        }
    }

    fun onInputMessageChange(newMessage: String) {
        uiState = uiState.copy(inputMessage = newMessage, errorMessage = null)
    }

    fun setErrorMessage(message: String?) {
        viewModelScope.launch {
            uiState = uiState.copy(errorMessage = message)
        }
    }

    fun sendMessage() {
        val message = uiState.inputMessage.trim()
        if (message.isEmpty()) return

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val response = repository.send(message)
                uiState = uiState.copy(responseMessage = response, inputMessage = "")
                ttsManager?.speak(response)
            } catch (_: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Não foi possível enviar a mensagem. Tente novamente."
                )
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager?.shutdown()
    }

    companion object {
        fun factory(
            repository: ConversationRepository,
            ttsManager: TextToSpeechManager
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    check(modelClass.isAssignableFrom(ConversationViewModel::class.java))
                    return ConversationViewModel(repository, ttsManager) as T
                }
            }
    }
}
