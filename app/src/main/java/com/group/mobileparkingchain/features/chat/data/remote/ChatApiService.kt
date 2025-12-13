package com.group.mobileparkingchain.features.chat.data.remote

import com.group.mobileparkingchain.features.chat.data.model.ChatRequest
import com.group.mobileparkingchain.features.chat.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}
