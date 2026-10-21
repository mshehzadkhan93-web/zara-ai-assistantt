package com.captain.aiassistant.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captain.aiassistant.data.AiApiClient
import com.captain.aiassistant.data.AppLauncher
import com.captain.aiassistant.data.ChatMessage
import com.captain.aiassistant.data.SettingsStore
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    var messages by mutableStateOf(
        listOf(
            ChatMessage(
                "assistant",
                "Hi! I am your AI assistant. Ask me anything, or say \"open YouTube\" to open an app."
            )
        )
    )
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun send(text: String, context: Context) {
        if (text.isBlank() || isLoading) return
        messages = messages + ChatMessage("user", text)

        // "Open apps" is handled on-device, before calling the AI.
        val openTarget = AppLauncher.parseOpenCommand(text)
        if (openTarget != null) {
            val ok = AppLauncher.openApp(context, openTarget)
            val reply = if (ok) {
                "Opening $openTarget..."
            } else {
                "Sorry, I could not find an app called \"$openTarget\" on this phone."
            }
            messages = messages + ChatMessage("assistant", reply)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val settings = SettingsStore(context)
                val client = AiApiClient(settings.geminiApiKey)
                val reply = client.chat(messages, settings.aiModel)
                messages = messages + ChatMessage("assistant", reply)
            } catch (e: Exception) {
                messages = messages + ChatMessage(
                    "assistant",
                    "Sorry, something went wrong: ${e.message}. " +
                        "Check your Gemini API key in Settings."
                )
            } finally {
                isLoading = false
            }
        }
    }
}
