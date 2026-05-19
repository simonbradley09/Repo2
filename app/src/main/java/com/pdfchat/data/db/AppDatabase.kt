package com.pdfchat.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pdfchat.data.db.entities.ChatMessageEntity
import com.pdfchat.data.db.entities.PdfDocumentEntity
import com.pdfchat.data.db.entities.TextChunkEntity

@Database(
    entities = [PdfDocumentEntity::class, TextChunkEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pdfDocumentDao(): PdfDocumentDao
    abstract fun textChunkDao(): TextChunkDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pdfchat_database"
                ).build().also { INSTANCE = it }
            }
    }
}
