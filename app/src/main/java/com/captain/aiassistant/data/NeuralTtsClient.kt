package com.captain.aiassistant.data

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * NATURAL voice output — deliberately NOT Android's built-in TextToSpeech.
 *
 * Gemini TTS, using the SAME Gemini API key as chat. The API returns
 * base64-encoded raw PCM audio (16-bit, 24 kHz, mono). We wrap it in a
 * standard WAV header so Android's MediaPlayer can play it. That is what
 * makes the voice sound human instead of robotic.
 *
 * Default voice "Aoede" is a documented Gemini prebuilt voice (breezy,
 * friendly). Other documented options: Kore (firm), Zephyr (bright),
 * Puck (upbeat), Charon (informative).
 */
class NeuralTtsClient(private val apiKey: String) {

    private val http = OkHttpClient()

    /** Returns playable WAV bytes of the spoken text. */
    suspend fun synthesize(
        text: String,
        voice: String,
        model: String = "gemini-2.5-flash-preview-tts"
    ): ByteArray = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", text))
                    )
                )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("responseModalities", JSONArray().put("AUDIO"))
                    .put(
                        "speechConfig",
                        JSONObject().put(
                            "voiceConfig",
                            JSONObject().put(
                                "prebuiltVoiceConfig",
                                JSONObject().put("voiceName", voice)
                            )
                        )
                    )
            )
            .toString()
        val request = Request.Builder()
            .url(
                "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "$model:generateContent?key=$apiKey"
            )
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        val response = http.newCall(request).execute()
        val raw = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
            throw Exception("Gemini voice error (HTTP ${response.code}): ${extractError(raw)}")
        }
        val data = JSONObject(raw)
            .optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optJSONObject("inlineData")
            ?.optString("data")
            .orEmpty()
        if (data.isBlank()) {
            throw Exception("Gemini voice returned no audio")
        }
        pcmToWav(Base64.decode(data, Base64.DEFAULT), sampleRate = 24000)
    }

    /** Wraps raw 16-bit mono PCM in a standard 44-byte WAV header. */
    private fun pcmToWav(pcm: ByteArray, sampleRate: Int): ByteArray {
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray(Charsets.US_ASCII))
        header.putInt(36 + pcm.size)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))
        header.put("fmt ".toByteArray(Charsets.US_ASCII))
        header.putInt(16) // PCM chunk size
        header.putShort(1) // audio format = PCM
        header.putShort(1) // mono
        header.putInt(sampleRate)
        header.putInt(sampleRate * 2) // byte rate
        header.putShort(2) // block align
        header.putShort(16) // bits per sample
        header.put("data".toByteArray(Charsets.US_ASCII))
        header.putInt(pcm.size)
        return header.array() + pcm
    }

    private fun extractError(raw: String): String {
        return try {
            JSONObject(raw).optJSONObject("error")?.optString("message")
                ?.takeIf { it.isNotBlank() } ?: raw.take(200)
        } catch (e: Exception) {
            raw.take(200)
        }
    }
}

/** Tiny WAV player for TTS replies. One sound at a time. */
class NeuralAudioPlayer {

    private var player: MediaPlayer? = null

    fun play(context: Context, audioBytes: ByteArray, onDone: () -> Unit) {
        stop()
        val file = File(context.cacheDir, "assistant_reply.wav")
        file.writeBytes(audioBytes)
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener { onDone() }
            prepare()
            start()
        }
    }

    fun stop() {
        player?.release()
        player = null
    }
}
