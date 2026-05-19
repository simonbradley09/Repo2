package com.pdfchat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pdfchat.data.db.entities.PdfDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDocumentDao {
    @Query("SELECT * FROM pdf_documents ORDER BY createdAt DESC")
    fun getAllPdfs(): Flow<List<PdfDocumentEntity>>

    @Query("SELECT * FROM pdf_documents ORDER BY createdAt DESC")
    suspend fun getAllPdfsOnce(): List<PdfDocumentEntity>

    @Insert
    suspend fun insert(pdf: PdfDocumentEntity): Long

    @Query("DELETE FROM pdf_documents WHERE id = :id")
    suspend fun deleteById(id: Long)
}
