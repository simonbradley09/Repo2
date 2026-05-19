package com.pdfchat.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pdfchat.data.db.entities.PdfDocumentEntity
import com.pdfchat.data.repository.PdfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PdfRepository(application)

    val pdfs: StateFlow<List<PdfDocumentEntity>> = repo.allPdfs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addPdf(uri: Uri, name: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                repo.addPdf(uri, name).onFailure { e ->
                    _error.value = "Failed to process \"$name\": ${e.message}"
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun deletePdf(id: Long) {
        viewModelScope.launch { repo.deletePdf(id) }
    }

    fun clearError() { _error.value = null }
}
