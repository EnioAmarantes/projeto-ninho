package io.projetoninho.client.conversation.data

import io.projetoninho.client.conversation.api.ConversationApi
import io.projetoninho.client.conversation.model.ConversationRequest

class ConversationRepositoryImpl(
    private val api: ConversationApi
) : ConversationRepository {

    override suspend fun send(message: String): String {
        val request = ConversationRequest(message = message)
        val response = api.send(request)
        return response.message
    }

}
