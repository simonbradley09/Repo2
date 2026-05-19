package com.pdfchat.data.retrieval

import com.pdfchat.data.db.entities.TextChunkEntity
import kotlin.math.ln

data class ScoredChunk(
    val chunk: TextChunkEntity,
    val pdfName: String,
    val score: Double
)

object Bm25Retriever {
    private const val K1 = 1.5
    private const val B = 0.75

    fun topChunks(
        query: String,
        chunks: List<TextChunkEntity>,
        pdfNames: Map<Long, String>,
        k: Int = 5
    ): List<ScoredChunk> {
        if (chunks.isEmpty()) return emptyList()
        val queryTerms = tokenize(query)
        if (queryTerms.isEmpty()) return emptyList()

        val tokenizedChunks = chunks.map { tokenize(it.content) }
        val avgLen = tokenizedChunks.map { it.size }.average()
        val n = chunks.size.toDouble()

        val idf = queryTerms.associateWith { term ->
            val docsWithTerm = tokenizedChunks.count { term in it }.toDouble()
            ln((n - docsWithTerm + 0.5) / (docsWithTerm + 0.5) + 1.0)
        }

        return chunks.mapIndexed { i, chunk ->
            val tokens = tokenizedChunks[i]
            val docLen = tokens.size.toDouble()
            val score = queryTerms.sumOf { term ->
                val tf = tokens.count { it == term }.toDouble()
                val numerator = tf * (K1 + 1)
                val denominator = tf + K1 * (1 - B + B * docLen / avgLen)
                (idf[term] ?: 0.0) * numerator / denominator
            }
            ScoredChunk(chunk, pdfNames[chunk.pdfId] ?: "Unknown PDF", score)
        }.sortedByDescending { it.score }.take(k)
    }

    private fun tokenize(text: String): List<String> =
        text.lowercase().split(Regex("[^a-z0-9]+")).filter { it.isNotBlank() }
}
