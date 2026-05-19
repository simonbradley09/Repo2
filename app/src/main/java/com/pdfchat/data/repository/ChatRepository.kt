package com.pdfchat.data.repository

import android.content.Context
import com.pdfchat.data.db.AppDatabase
import com.pdfchat.data.db.entities.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val allMessages: Flow<List<ChatMessageEntity>> = db.chatMessageDao().getAllMessages()

    suspend fun insertMessage(role: String, content: String): Long = withContext(Dispatchers.IO) {
        db.chatMessageDao().insert(ChatMessageEntity(role = role, content = content))
    }

    suspend fun getRecentMessages(limit: Int = 20): List<ChatMessageEntity> =
        withContext(Dispatchers.IO) {
            db.chatMessageDao().getRecentMessages(limit).reversed()
        }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        db.chatMessageDao().deleteAll()
    }
}
