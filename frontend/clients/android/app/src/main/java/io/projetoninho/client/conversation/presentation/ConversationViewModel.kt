package io.projetoninho.client.conversation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.projetoninho.client.conversation.data.ConversationRepository
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: ConversationRepository
) : ViewModel() {

    var uiState by mutableStateOf(ConversationUiState())
        private set

    fun onInputMessageChange(newMessage: String) {
        uiState = uiState.copy(inputMessage = newMessage, errorMessage = null)
    }

    fun sendMessage() {
        val message = uiState.inputMessage.trim()
        if (message.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = repository.send(message)
                uiState = uiState.copy(responseMessage = response, inputMessage = "")
            } catch (_: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Não foi possível enviar a mensagem. Tente novamente."
                )
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    companion object {
        fun factory(repository: ConversationRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    check(modelClass.isAssignableFrom(ConversationViewModel::class.java))
                    return ConversationViewModel(repository) as T
                }
            }
    }
}
