package com.group.mobileparkingchain.features.chat.data.repository

import com.group.mobileparkingchain.features.chat.data.model.ChatRequest
import com.group.mobileparkingchain.features.chat.data.model.ChatResponse
import com.group.mobileparkingchain.features.chat.data.remote.ChatApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ChatRepository(private val apiService: ChatApiService) {
    
    fun sendMessage(query: String): Flow<Result<ChatResponse>> = flow {
        try {
            val response = apiService.sendMessage(ChatRequest(query))
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: HttpException) {
            emit(Result.failure(Exception("Network Error: ${e.message()}")))
        } catch (e: IOException) {
            emit(Result.failure(Exception("Connection Error: Please check your internet")))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
