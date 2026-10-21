package com.captain.aiassistant.data

import android.content.Context

/**
 * Stores the user's Gemini setup on the phone only (SharedPreferences).
 *
 * ONE key powers everything: chat, natural voice AND news. There are no
 * other provider keys in this app anymore.
 *
 * HOW THE KEY GETS HERE: the user types it in the app's Settings screen.
 * It is never sent anywhere except to Google's Gemini API, and it must
 * never be pasted into any chat.
 */
class SettingsStore(context: Context) {

    private val prefs =
        context.getSharedPreferences("ai_assistant_settings", Context.MODE_PRIVATE)

    /** Gemini API key — free from Google AI Studio (https://aistudio.google.com). */
    var geminiApiKey: String
        get() = prefs.getString("gemini_api_key", "") ?: ""
        set(value) = prefs.edit().putString("gemini_api_key", value).apply()

    /** Chat model. Default is fast and low-cost: gemini-2.0-flash. */
    var aiModel: String
        get() = prefs.getString("ai_model", "gemini-2.0-flash") ?: "gemini-2.0-flash"
        set(value) = prefs.edit().putString("ai_model", value).apply()

    /** Gemini prebuilt voice for spoken replies. Aoede = breezy, friendly. */
    var ttsVoice: String
        get() = prefs.getString("tts_voice", "Aoede") ?: "Aoede"
        set(value) = prefs.edit().putString("tts_voice", value).apply()

    /** Gemini TTS model. */
    var ttsModel: String
        get() = prefs.getString("tts_model", "gemini-2.5-flash-preview-tts")
            ?: "gemini-2.5-flash-preview-tts"
        set(value) = prefs.edit().putString("tts_model", value).apply()
}
