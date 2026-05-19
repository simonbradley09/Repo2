package com.pdfchat.data.pdf

data class TextChunk(
    val pageNumber: Int,
    val chunkIndex: Int,
    val content: String
)

object TextChunker {
    private const val CHUNK_SIZE = 400
    private const val OVERLAP = 50

    fun chunk(pages: List<Pair<Int, String>>): List<TextChunk> {
        val result = mutableListOf<TextChunk>()
        for ((pageNumber, text) in pages) {
            if (text.isBlank()) continue
            val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
            var start = 0
            var chunkIndex = 0
            while (start < words.size) {
                val end = minOf(start + CHUNK_SIZE, words.size)
                val content = words.subList(start, end).joinToString(" ")
                if (content.isNotBlank()) {
                    result.add(TextChunk(pageNumber, chunkIndex, content))
                    chunkIndex++
                }
                if (end == words.size) break
                start += CHUNK_SIZE - OVERLAP
            }
        }
        return result
    }
}
