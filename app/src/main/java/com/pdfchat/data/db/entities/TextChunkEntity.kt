package com.pdfchat.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "text_chunks",
    foreignKeys = [ForeignKey(
        entity = PdfDocumentEntity::class,
        parentColumns = ["id"],
        childColumns = ["pdfId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pdfId")]
)
data class TextChunkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pdfId: Long,
    val content: String,
    val pageNumber: Int,
    val chunkIndex: Int
)
