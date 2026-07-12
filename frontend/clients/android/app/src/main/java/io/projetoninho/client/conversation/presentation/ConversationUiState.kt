package io.projetoninho.client.conversation.presentation

data class ConversationUiState(
    val inputMessage: String = "",
    val responseMessage: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
