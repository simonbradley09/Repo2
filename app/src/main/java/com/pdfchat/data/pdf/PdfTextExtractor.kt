package com.pdfchat.data.pdf

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

class PdfTextExtractor(private val context: Context) {

    fun extractPages(uri: Uri): List<Pair<Int, String>> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Cannot open PDF: $uri")
        return inputStream.use { stream ->
            PDDocument.load(stream).use { doc ->
                val stripper = PDFTextStripper()
                (1..doc.numberOfPages).map { pageNum ->
                    stripper.startPage = pageNum
                    stripper.endPage = pageNum
                    pageNum to stripper.getText(doc).trim()
                }
            }
        }
    }

    fun pageCount(uri: Uri): Int {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return 0
        return inputStream.use { stream ->
            PDDocument.load(stream).use { it.numberOfPages }
        }
    }
}
