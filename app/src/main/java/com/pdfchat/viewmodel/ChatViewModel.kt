package com.pdfchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pdfchat.data.api.ApiMessage
import com.pdfchat.data.api.ClaudeApiClient
import com.pdfchat.data.db.entities.ChatMessageEntity
import com.pdfchat.data.preferences.SecurePreferences
import com.pdfchat.data.repository.ChatRepository
import com.pdfchat.data.repository.PdfRepository
import com.pdfchat.data.retrieval.Bm25Retriever
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepo = ChatRepository(application)
    private val pdfRepo = PdfRepository(application)
    private val apiClient = ClaudeApiClient()
    private val prefs = SecurePreferences(application)

    val messages: StateFlow<List<ChatMessageEntity>> = chatRepo.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val hasApiKey: Boolean get() = prefs.apiKey.isNotBlank()

    fun sendMessage(userText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                chatRepo.insertMessage("user", userText)

                val chunks = pdfRepo.getAllChunks()
                val pdfNames = pdfRepo.getPdfNames()
                val topChunks = Bm25Retriever.topChunks(userText, chunks, pdfNames, k = 5)

                val contextBlock = if (topChunks.isNotEmpty()) {
                    topChunks.joinToString("\n\n") { sc ->
                        "--- From \"${sc.pdfName}\" (page ${sc.chunk.pageNumber}) ---\n${sc.chunk.content}"
                    }
                } else {
                    "(No PDF content loaded — please add PDFs in the Library tab.)"
                }

                // Get recent history excluding the message we just inserted (last entry)
                val recentHistory = chatRepo.getRecentMessages(limit = 21)
                val historyWithoutCurrent = recentHistory.dropLast(1)

                val apiMessages = historyWithoutCurrent.map { msg ->
                    ApiMessage(msg.role, msg.content)
                } + ApiMessage(
                    role = "user",
                    content = "[Context]\n$contextBlock\n\nQuestion: $userText"
                )

                val systemPrompt = """
                    You are a helpful assistant. Answer questions using ONLY the context
                    provided from the user's PDF documents. If the answer isn't in the
                    context, say so. Cite document names and page numbers when possible.
                """.trimIndent()

                apiClient.sendMessage(prefs.apiKey, apiMessages, systemPrompt)
                    .onSuccess { response -> chatRepo.insertMessage("assistant", response) }
                    .onFailure { e ->
                        _error.value = e.message ?: "Request failed"
                        chatRepo.insertMessage("assistant", "Error: ${e.message}")
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }

    fun clearHistory() {
        viewModelScope.launch { chatRepo.clearHistory() }
    }
}
