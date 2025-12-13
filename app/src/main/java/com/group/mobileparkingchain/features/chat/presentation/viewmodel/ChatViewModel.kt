package com.group.mobileparkingchain.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.chat.data.model.ChatMessage
import com.group.mobileparkingchain.features.chat.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(query: String) {
        if (query.isBlank()) return

        val userMessage = ChatMessage(query, isUser = true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            repository.sendMessage(query).collect { result ->
                _isLoading.value = false
                result.onSuccess { response ->
                    val botMessage = ChatMessage(response.answer, isUser = false)
                    _messages.value = _messages.value + botMessage
                }.onFailure { error ->
                    val errorMessage = ChatMessage("Failed to get response: ${error.message}", isUser = false)
                    _messages.value = _messages.value + errorMessage
                }
            }
        }
    }
}

class ChatViewModelFactory(private val repository: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
