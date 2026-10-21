package com.captain.aiassistant.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captain.aiassistant.data.AiApiClient
import com.captain.aiassistant.data.AppLauncher
import com.captain.aiassistant.data.ChatMessage
import com.captain.aiassistant.data.NeuralAudioPlayer
import com.captain.aiassistant.data.NeuralTtsClient
import com.captain.aiassistant.data.SettingsStore
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * LIVE CHAT: listens with Android's SpeechRecognizer (input only),
 * thinks with Gemini, and answers with a NATURAL Gemini voice —
 * never Android's built-in robotic TextToSpeech.
 */
class VoiceViewModel : ViewModel() {

    var status by mutableStateOf("Tap the mic and speak")
        private set
    var userSpeech by mutableStateOf("")
        private set
    var assistantReply by mutableStateOf("")
        private set
    var isBusy by mutableStateOf(false)
        private set

    private val player = NeuralAudioPlayer()

    fun startListening(context: Context) {
        if (isBusy) return
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            status = "Speech recognition is not available on this phone."
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        status = "Listening..."
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                recognizer.destroy()
                if (text.isBlank()) {
                    status = "I didn't catch that. Try again."
                    return
                }
                userSpeech = text
                answerWithVoice(text, context)
            }

            override fun onError(error: Int) {
                recognizer.destroy()
                status = "Listening failed (error $error). Try again."
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        recognizer.startListening(intent)
    }

    private fun answerWithVoice(text: String, context: Context) {
        // "Open apps" also works by voice, on-device.
        val openTarget = AppLauncher.parseOpenCommand(text)
        if (openTarget != null) {
            val ok = AppLauncher.openApp(context, openTarget)
            val reply = if (ok) "Opening $openTarget." else
                "Sorry, I could not find $openTarget on this phone."
            assistantReply = reply
            speak(reply, context)
            return
        }

        isBusy = true
        status = "Thinking..."
        viewModelScope.launch {
            try {
                val settings = SettingsStore(context)
                val client = AiApiClient(settings.geminiApiKey)
                val reply = client.chat(
                    listOf(ChatMessage("user", text)),
                    settings.aiModel
                )
                assistantReply = reply
                speak(reply, context)
            } catch (e: Exception) {
                status = "Error: ${e.message}. Check your API key in Settings."
                isBusy = false
            }
        }
    }

    private fun speak(text: String, context: Context) {
        status = "Speaking..."
        viewModelScope.launch {
            try {
                val settings = SettingsStore(context)
                val tts = NeuralTtsClient(settings.geminiApiKey)
                val audio = tts.synthesize(text, settings.ttsVoice, settings.ttsModel)
                player.play(context, audio) {
                    status = "Tap the mic and speak"
                    isBusy = false
                }
            } catch (e: Exception) {
                status = "Voice error: ${e.message}. " +
                    "Check your Gemini API key in Settings."
                isBusy = false
            }
        }
    }

    fun stopSpeaking() {
        player.stop()
        isBusy = false
        status = "Tap the mic and speak"
    }

    override fun onCleared() {
        player.stop()
        super.onCleared()
    }
}
