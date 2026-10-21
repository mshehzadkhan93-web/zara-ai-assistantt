package com.captain.aiassistant.data

/** One line in the chat history. Role is "user" or "assistant". */
data class ChatMessage(
    val role: String,
    val content: String
) {
    val isUser: Boolean get() = role == "user"
}
