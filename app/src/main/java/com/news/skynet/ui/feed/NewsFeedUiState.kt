package com.news.skynet.ui.feed

import com.news.skynet.domain.model.NewsArticle

/**
 * NewsFeedUiState.kt
 *
 * Represents all possible states of the News Feed screen.
 * The ViewModel exposes this as a [StateFlow] so the Fragment can react
 * to every state transition in a structured, exhaustive way.
 */
sealed class NewsFeedUiState {
    /** Initial state before any data has been requested. */
    object Idle : NewsFeedUiState()

    /** Network request is in-flight; show shimmer skeleton. */
    object Loading : NewsFeedUiState()

    /** Articles are available; render the list. */
    data class Success(val articles: List<NewsArticle>) : NewsFeedUiState()

    /** No articles and network error; show error UI with retry button. */
    data class Error(val message: String) : NewsFeedUiState()

    /** Feed loaded successfully but the list is empty. */
    object Empty : NewsFeedUiState()
}
