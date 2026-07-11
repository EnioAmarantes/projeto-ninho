package io.projetoninho.client.conversation.data

interface ConversationRepository {

    suspend fun send(
        message: String
    ): String

}