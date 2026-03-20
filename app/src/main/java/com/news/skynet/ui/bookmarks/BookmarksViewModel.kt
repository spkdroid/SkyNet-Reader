package com.news.skynet.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.domain.usecase.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BookmarksViewModel.kt
 *
 * Exposes the list of saved articles as a [StateFlow] of a simple sealed
 * state. The [stateIn] operator with [SharingStarted.WhileSubscribed] ensures
 * the Room Flow is only active while the UI is visible.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    getBookmarksUseCase: GetBookmarksUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> =
        getBookmarksUseCase()
            .map { articles ->
                if (articles.isEmpty()) BookmarksUiState.Empty
                else BookmarksUiState.Success(articles)
            }
            .stateIn(
                scope  = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BookmarksUiState.Loading
            )

    fun removeBookmark(article: NewsArticle) {
        viewModelScope.launch { bookmarkArticleUseCase(article) }
    }
}

sealed class BookmarksUiState {
    object Loading   : BookmarksUiState()
    object Empty     : BookmarksUiState()
    data class Success(val bookmarks: List<NewsArticle>) : BookmarksUiState()
}
