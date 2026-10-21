package com.captain.aiassistant.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captain.aiassistant.data.NewsArticle
import com.captain.aiassistant.data.NewsRepository
import com.captain.aiassistant.data.SettingsStore
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    var articles by mutableStateOf<List<NewsArticle>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun load(context: Context) {
        if (isLoading) return
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val settings = SettingsStore(context)
                if (settings.geminiApiKey.isBlank()) {
                    error = "Add your Gemini API key in Settings first " +
                        "(free from Google AI Studio)."
                    return@launch
                }
                articles = NewsRepository().getHeadlines(
                    settings.geminiApiKey,
                    settings.aiModel
                )
                if (articles.isEmpty()) {
                    error = "No headlines found. Try again later."
                }
            } catch (e: Exception) {
                error = "Could not load news: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
