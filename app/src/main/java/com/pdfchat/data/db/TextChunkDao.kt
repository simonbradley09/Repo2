package com.pdfchat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pdfchat.data.db.entities.TextChunkEntity

@Dao
interface TextChunkDao {
    @Query("SELECT * FROM text_chunks")
    suspend fun getAllChunks(): List<TextChunkEntity>

    @Insert
    suspend fun insertAll(chunks: List<TextChunkEntity>)

    @Query("DELETE FROM text_chunks WHERE pdfId = :pdfId")
    suspend fun deleteByPdfId(pdfId: Long)
}
