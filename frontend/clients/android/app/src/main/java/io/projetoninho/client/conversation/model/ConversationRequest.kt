package io.projetoninho.client.conversation.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationRequest(
    val message: String
)