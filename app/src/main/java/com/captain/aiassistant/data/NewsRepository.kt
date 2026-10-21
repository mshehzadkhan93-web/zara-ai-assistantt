package com.captain.aiassistant.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/** One headline card. url may be null if no source link was grounded. */
data class NewsSource(val name: String?)

data class NewsArticle(
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val source: NewsSource?
)

/**
 * Headlines from Gemini with Google Search grounding — no separate news
 * key needed. The SAME Gemini API key powers this tab too.
 *
 * How it works: Gemini is asked for today's top headlines with the
 * googleSearch grounding tool enabled, so its answer is based on live
 * web results. Real source links come from the grounding chunks; if
 * grounding is unavailable, the reply text itself is parsed into cards.
 */
class NewsRepository {

    private val http = OkHttpClient()

    suspend fun getHeadlines(apiKey: String, model: String): List<NewsArticle> =
        withContext(Dispatchers.IO) {
            val prompt =
                "List today's top 10 world headlines. For each headline, " +
                    "write exactly one line in this format: " +
                    "TITLE | one-line summary | source publication name. " +
                    "Use current, up-to-date information."
            val body = JSONObject()
                .put(
                    "contents",
                    JSONArray().put(
                        JSONObject().put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", prompt))
                        )
                    )
                )
                .put(
                    "tools",
                    JSONArray().put(JSONObject().put("googleSearch", JSONObject()))
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
                throw Exception("Gemini news error (HTTP ${response.code}): ${extractError(raw)}")
            }
            val root = JSONObject(raw)
            val firstCandidate = root.optJSONArray("candidates")?.optJSONObject(0)
            val text = StringBuilder()
            val parts = firstCandidate
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    text.append(parts.optJSONObject(i)?.optString("text").orEmpty())
                }
            }

            val articles = parseLines(text.toString()).toMutableList()

            // Attach real source URLs from grounding chunks where titles match.
            val chunks = firstCandidate
                ?.optJSONObject("groundingMetadata")
                ?.optJSONArray("groundingChunks")
            if (chunks != null) {
                for (i in 0 until chunks.length()) {
                    val web = chunks.optJSONObject(i)?.optJSONObject("web") ?: continue
                    val uri = web.optString("uri").takeIf { it.isNotBlank() } ?: continue
                    val chunkTitle = web.optString("title").orEmpty()
                    if (chunkTitle.isBlank()) continue
                    val matchIndex = articles.indexOfFirst { article ->
                        val title = article.title.orEmpty()
                        title.isNotBlank() &&
                            (title.contains(chunkTitle.take(30), ignoreCase = true) ||
                                chunkTitle.contains(title.take(30), ignoreCase = true))
                    }
                    if (matchIndex >= 0) {
                        val match = articles[matchIndex]
                        if (match.url == null) {
                            articles[matchIndex] = match.copy(url = uri)
                        }
                    } else {
                        articles.add(
                            NewsArticle(chunkTitle, null, uri, null, null)
                        )
                    }
                }
            }

            articles
                .distinctBy { it.title?.lowercase().orEmpty() }
                .filter { !it.title.isNullOrBlank() }
                .take(12)
        }

    /** Parses "TITLE | summary | source" lines; falls back to plain lines. */
    private fun parseLines(text: String): List<NewsArticle> {
        val structured = mutableListOf<NewsArticle>()
        val plain = mutableListOf<String>()
        for (rawLine in text.lines()) {
            var line = rawLine.trim()
            // Strip list markers like "1. ", "- ", "* ".
            line = line.replace(Regex("^(\\d+[.)]|[-*])\\s+"), "").trim()
            if (line.isBlank()) continue
            if (line.contains("|")) {
                val fields = line.split("|").map { it.trim() }
                if (fields[0].isNotBlank()) {
                    structured.add(
                        NewsArticle(
                            title = fields[0],
                            description = fields.getOrNull(1)?.takeIf { it.isNotBlank() },
                            url = null,
                            urlToImage = null,
                            source = fields.getOrNull(2)
                                ?.takeIf { it.isNotBlank() }
                                ?.let { NewsSource(it) }
                        )
                    )
                }
            } else if (!line.startsWith("Here", ignoreCase = true)) {
                plain.add(line)
            }
        }
        if (structured.isNotEmpty()) return structured
        return plain.map { NewsArticle(it, null, null, null, null) }
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
