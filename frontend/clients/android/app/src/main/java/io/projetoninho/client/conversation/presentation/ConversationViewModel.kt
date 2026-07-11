package io.projetoninho.client.conversation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.data.ConversationRepository
import io.projetoninho.client.conversation.data.ConversationRepositoryImpl
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: ConversationRepository = ConversationRepositoryImpl(ConversationApi())
) : ViewModel() {

    var inputMessage by mutableStateOf("")
        private set

    var responseMessage by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onInputMessageChange(newMessage: String) {
        inputMessage = newMessage
    }

    fun sendMessage() {
        if (inputMessage.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.send(inputMessage)
                responseMessage = response
                inputMessage = ""
            } catch (e: Exception) {
                responseMessage = "Erro: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
