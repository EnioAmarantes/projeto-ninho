package io.projetoninho.client.conversation.api

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.projetoninho.client.BuildConfig
import io.projetoninho.client.conversation.model.ConversationRequest
import io.projetoninho.client.conversation.model.ConversationResponse
import io.ktor.client.HttpClient

class ConversationApi(
    private val client: HttpClient,
    private val baseUrl: String = BuildConfig.API_BASE_URL
) {

    suspend fun send(request: ConversationRequest): ConversationResponse {
        return client.post("${baseUrl.trimEnd('/')}/conversation") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}

