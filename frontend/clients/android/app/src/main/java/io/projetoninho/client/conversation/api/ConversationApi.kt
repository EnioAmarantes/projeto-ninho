package io.projetoninho.client.conversation.api

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.projetoninho.client.conversation.model.ConversationRequest
import io.projetoninho.client.conversation.model.ConversationResponse
import io.projetoninho.client.core.network.ProjetoNinhoClient

class ConversationApi {

    suspend fun send(request: ConversationRequest): ConversationResponse {
        val baseUrl = "http://192.168.0.7:5011/api/"
        return ProjetoNinhoClient.http.post(baseUrl + "conversation") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

}


