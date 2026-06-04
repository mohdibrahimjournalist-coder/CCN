package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.NewsDatabase
import com.example.data.NewsRepository
import com.example.data.NewsScriptDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NewsDatabase.getDatabase(application)
    private val repository = NewsRepository(database.newsScriptDao())

    val savedDrafts: StateFlow<List<NewsScriptDraft>> = repository.allDrafts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _aiScriptText = MutableStateFlow("")
    val aiScriptText = _aiScriptText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _genError = MutableStateFlow<String?>(null)
    val genError = _genError.asStateFlow()

    fun generateAiScript(topic: String, category: String, tone: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            _genError.value = null
            _aiScriptText.value = ""

            val prompt = """
                You are a professional television news anchor for CCN Channel. 
                Write a highly compelling, broadcast-ready teleprompter script for our live broadcast.
                
                Topic / Outline: $topic
                News Category: $category
                Broadcast Style / Tone: $tone
                
                Format rules:
                - Start with a clear anchor lead-in (e.g., "[ANCHOR INTRO] From our CCN Live Desk...")
                - Write in a natural, spoken rhythm, with pauses indicated where needed (e.g. ... [PAUSE] ...)
                - Do not include markdown bold headers inside the main read blocks, as it ruins teleprompter aesthetics. Use simple CAPS for voice emphasis where necessary.
                - End with a professional news sign-off: "I'm [ANCHOR NAME] for CCN Channel. Back to you in the studio." or similar.
                - Keep it engaging, authoritative, and about 2-3 minutes of rapid-read spoken text.
            """.trimIndent()

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _genError.value = "Gemini API key is not set. Please configure it in your Secrets Panel (GEMINI_API_KEY)."
                    _isGenerating.value = false
                    return@launch
                }

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    _aiScriptText.value = responseText
                } else {
                    _genError.value = "Received empty response from Gemini API."
                }
            } catch (e: Exception) {
                _genError.value = "Failed to communicate with CCN AI engine: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveScript(title: String, body: String, category: String) {
        viewModelScope.launch {
            repository.insert(NewsScriptDraft(title = title, body = body, category = category))
        }
    }

    fun deleteScript(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearAllScripts() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}
