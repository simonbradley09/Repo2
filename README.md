# PDF Chat — Android RAG App
A local-first Android application that lets users chat with PDF documents using Retrieval-Augmented Generation (RAG). The app performs document parsing, chunking, indexing, and retrieval entirely on-device using BM25 retrieval, while leveraging the Anthropic Claude API for final answer generation.
---
## Overview
PDF Chat is designed around a privacy-conscious, local-first architecture:
* **On-device retrieval pipeline** using BM25 keyword scoring
* **Local PDF processing** with chunk storage in SQLite/Room
* **Jetpack Compose UI** with modern Android architecture
* **Claude API integration** for response generation
* **Secure API key storage** using AndroidX Security with AES-256-GCM
Only the final assembled prompt is sent to the LLM API.
---
# Features
* Import and process PDF files locally
* Ask questions about uploaded documents
* BM25-based retrieval engine implemented in Kotlin
* Context-aware prompt assembly
* Conversation history persistence
* Local Room database storage
* Modern Jetpack Compose UI
* Encrypted API key management
* Bottom navigation architecture
* Source-aware responses with citations
---
# Tech Stack
## Android
* Kotlin
* Jetpack Compose
* Android ViewModel
* StateFlow
* Room Database
* Coroutines
* Compose Navigation
## PDF & Retrieval
* PdfBox Android
* BM25 Retrieval
* Custom text chunking pipeline
## AI
* Anthropic Claude API
* Claude Sonnet model
## Security
* AndroidX Security
* AES-256-GCM encrypted preferences
---
# Requirements
| Requirement     | Details                               |
| --------------- | ------------------------------------- |
| Android Version | Android 10+ (API 29 minimum)          |
| Target SDK      | API 35                                |
| API Key         | Anthropic API key                     |
| Internet Access | Required only for Claude API requests |
| Storage Access  | PDF file access permissions           |
---
# Architecture
The application follows a layered architecture:
```text
UI Layer
├── ChatScreen
├── LibraryScreen
├── SettingsScreen
└── AppNavigation
ViewModel Layer
├── ChatViewModel
└── LibraryViewModel
Repository Layer
├── ChatRepository
└── PdfRepository
Data Layer
├── Room Database
├── PdfTextExtractor
├── TextChunker
├── Bm25Retriever
├── ClaudeApiClient
└── SecurePreferences
```
---
# RAG Pipeline
## 1. User Sends Message
The user submits a question through the Compose chat UI.
## 2. Message Persistence
The message is stored in Room for conversation history.
## 3. BM25 Retrieval
* All chunks are loaded from local storage
* BM25 scores each chunk against the query
* Top matching chunks are selected
Current tuning:
```text
k1 = 1.5
b = 0.75
Top K = 5 chunks
```
## 4. Prompt Assembly
The app constructs a prompt containing:
* System instructions
* Retrieved document chunks
* Source metadata
* Previous conversation history
* Current user query
## 5. Claude API Request
The final prompt is sent to Anthropic Claude.
## 6. Response Persistence
The generated answer is saved and displayed in the chat UI.
---
# PDF Processing Flow
```text
PDF File
    ↓
PdfBox Extraction
    ↓
Text Chunking
    ↓
Chunk Storage
    ↓
BM25 Retrieval
    ↓
Prompt Assembly
    ↓
Claude Response
```
---
# Text Chunking Strategy
The app uses overlapping chunks for retrieval quality.
```text
Chunk Size: 400 words
Overlap: 50 words
```
Each chunk stores:
* Source PDF name
* Page number
* Chunk text
---
# Security
API keys are stored securely using encrypted shared preferences.
```text
Encryption: AES-256-GCM
Library: AndroidX Security
```
The app never stores API keys in plaintext.
---
# Performance Characteristics
## Advantages
* Fully local retrieval pipeline
* Minimal cloud dependency
* Simple and understandable architecture
* Fast keyword matching
* Low infrastructure complexity
## Limitations
### BM25 Is Keyword-Based
BM25 works well for exact or similar wording but may miss semantic matches.
Example:
```text
Question: "financial risks"
Document: "fiscal exposure"
```
A semantic embedding system would perform better for paraphrased concepts.
### Memory Usage
The current implementation loads all chunks into memory during retrieval.
This may become expensive with:
* Large PDF libraries
* Very large documents
* High chunk counts
### Stateless LLM Calls
Claude has no persistent memory between requests.
Only the current request context is visible to the model.
---
# Suggested Future Improvements
## Retrieval
* Add vector embeddings
* Hybrid BM25 + semantic search
* Document-level pre-filtering
* Incremental indexing
## Performance
* Streaming responses
* Background indexing
* Pagination for large datasets
* Caching retrieval results
## UX
* PDF highlighting
* Inline citations
* Multi-document filtering
* Search history
## AI
* Multi-model support
* Local embedding generation
* Reranking pipeline
---
# Example Project Structure
```text
app/
├── ui/
│   ├── screens/
│   ├── components/
│   └── navigation/
│
├── viewmodel/
│   ├── ChatViewModel.kt
│   └── LibraryViewModel.kt
│
├── repository/
│   ├── ChatRepository.kt
│   └── PdfRepository.kt
│
├── data/
│   ├── database/
│   ├── retrieval/
│   ├── chunking/
│   └── extraction/
│
├── network/
│   └── ClaudeApiClient.kt
│
└── security/
    └── SecurePreferences.kt
```
---
# API Usage
The application communicates with Anthropic using the Messages API.
Example:
```http
POST /v1/messages
```
The request includes:
* System prompt
* Retrieved chunks
* Conversation history
* User query
---
# Why This Architecture?
This project intentionally favors:
* Simplicity
* Local processing
* Explainability
* Low infrastructure overhead
* Android-native implementation
Instead of relying on complex vector databases or server-side orchestration.
---
# License
Add your preferred license here.
Example:
```text
MIT License
```
---
# Credits
Built with:
* Jetpack Compose
* AndroidX
* Apache PDFBox
* Anthropic Claude API
---
# Summary
PDF Chat demonstrates a practical Android-native RAG architecture with:
* Local PDF ingestion
* On-device retrieval
* BM25 ranking
* Claude-powered generation
* Secure key handling
* Modern Compose architecture
It is intentionally lightweight, understandable, and easy to extend into more advanced retrieval systems later.
