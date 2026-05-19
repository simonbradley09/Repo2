package com.pdfchat.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pdfchat.data.db.AppDatabase
import com.pdfchat.data.db.entities.PdfDocumentEntity
import com.pdfchat.data.db.entities.TextChunkEntity
import com.pdfchat.data.pdf.PdfTextExtractor
import com.pdfchat.data.pdf.TextChunker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PdfRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val extractor = PdfTextExtractor(context)

    val allPdfs: Flow<List<PdfDocumentEntity>> = db.pdfDocumentDao().getAllPdfs()

    suspend fun addPdf(uri: Uri, name: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val pages = extractor.extractPages(uri)
            val docId = db.pdfDocumentDao().insert(
                PdfDocumentEntity(name = name, uri = uri.toString(), pageCount = pages.size)
            )
            val chunks = TextChunker.chunk(pages).map { chunk ->
                TextChunkEntity(
                    pdfId = docId,
                    content = chunk.content,
                    pageNumber = chunk.pageNumber,
                    chunkIndex = chunk.chunkIndex
                )
            }
            db.textChunkDao().insertAll(chunks)
        }
    }

    suspend fun deletePdf(id: Long) = withContext(Dispatchers.IO) {
        db.pdfDocumentDao().deleteById(id)
    }

    suspend fun getAllChunks(): List<TextChunkEntity> = withContext(Dispatchers.IO) {
        db.textChunkDao().getAllChunks()
    }

    suspend fun getPdfNames(): Map<Long, String> = withContext(Dispatchers.IO) {
        db.pdfDocumentDao().getAllPdfsOnce().associate { it.id to it.name }
    }
}
