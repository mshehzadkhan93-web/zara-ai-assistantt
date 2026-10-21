package com.captain.aiassistant.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * "Open apps" feature. Runs fully on the phone — no internet, no cost.
 * Say or type "open YouTube" and the assistant opens that app.
 */
object AppLauncher {

    /** Common apps, so the well-known ones always resolve instantly. */
    private val KNOWN_APPS = mapOf(
        "youtube" to "com.google.android.youtube",
        "whatsapp" to "com.whatsapp",
        "facebook" to "com.facebook.katana",
        "instagram" to "com.instagram.android",
        "chrome" to "com.android.chrome",
        "gmail" to "com.google.android.gm",
        "maps" to "com.google.android.apps.maps",
        "camera" to "com.android.camera",
        "spotify" to "com.spotify.music",
        "tiktok" to "com.zhiliaoapp.musically",
        "telegram" to "org.telegram.messenger",
        "play store" to "com.android.vending",
        "settings" to "com.android.settings",
        "phone" to "com.android.dialer",
        "messages" to "com.google.android.apps.messaging",
        "photos" to "com.google.android.apps.photos",
        "drive" to "com.google.android.apps.docs"
    )

    /**
     * If the message is an "open ..." command, returns the app name.
     * Otherwise returns null. Examples: "open YouTube" -> "youtube".
     */
    fun parseOpenCommand(text: String): String? {
        val clean = text.trim().lowercase()
        if (!clean.startsWith("open ")) return null
        return clean.removePrefix("open ").trim().takeIf { it.isNotEmpty() }
    }

    /** Opens the app by name. Returns true if it launched. */
    fun openApp(context: Context, appName: String): Boolean {
        val wanted = appName.trim().lowercase()
        val packageName = KNOWN_APPS[wanted]
            ?: findInstalledApp(context, wanted)
            ?: return false
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    /** Fallback: match by the app's display name on the phone. */
    private fun findInstalledApp(context: Context, wanted: String): String? {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            val label = pm.getApplicationLabel(app).toString().lowercase()
            if ((label == wanted || label.contains(wanted)) &&
                pm.getLaunchIntentForPackage(app.packageName) != null
            ) {
                return app.packageName
            }
        }
        return null
    }
}
