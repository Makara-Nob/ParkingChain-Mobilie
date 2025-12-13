package com.group.mobileparkingchain.features.chat.data.model

data class ChatRequest(
    val query: String
)

data class ChatResponse(
    val answer: String,
    val source_documents: List<SourceDocument>? = null
)

data class SourceDocument(
    val source: String,
    val content: String
)

// For UI State
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
