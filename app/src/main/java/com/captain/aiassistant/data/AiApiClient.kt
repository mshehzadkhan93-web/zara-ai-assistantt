package com.captain.aiassistant.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * The AI "brain" — Google Gemini ONLY.
 *
 * One Gemini API key (typed in the app's Settings screen) powers chat,
 * natural voice AND news. The key is sent as a ?key= query parameter,
 * exactly how the Gemini API expects it. No other provider is used.
 */
class AiApiClient(private val apiKey: String) {

    private val http = OkHttpClient()

    /** Sends the chat history and returns the assistant's reply text. */
    suspend fun chat(history: List<ChatMessage>, model: String): String =
        withContext(Dispatchers.IO) {
            val contents = JSONArray()
            for (msg in history) {
                if (msg.content.isBlank()) continue
                // Gemini roles are "user" and "model" (not "assistant").
                val role = if (msg.role == "assistant") "model" else "user"
                contents.put(
                    JSONObject()
                        .put("role", role)
                        .put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", msg.content))
                        )
                )
            }
            val body = JSONObject()
                .put(
                    "systemInstruction",
                    JSONObject().put(
                        "parts",
                        JSONArray().put(
                            JSONObject().put(
                                "text",
                                "You are a helpful, friendly AI assistant. " +
                                    "Keep answers short and clear."
                            )
                        )
                    )
                )
                .put("contents", contents)
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
                throw Exception("Gemini error (HTTP ${response.code}): ${extractError(raw)}")
            }
            val text = StringBuilder()
            val parts = JSONObject(raw)
                .optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    text.append(parts.optJSONObject(i)?.optString("text").orEmpty())
                }
            }
            val reply = text.toString().trim()
            if (reply.isEmpty()) "Sorry, I got an empty reply." else reply
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
