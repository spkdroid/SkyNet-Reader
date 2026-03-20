package com.news.skynet.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.domain.usecase.GetNewsFeedUseCase
import com.news.skynet.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NewsFeedViewModel.kt
 *
 * ViewModel for the News Feed screen. It is scoped to the Fragment lifecycle
 * via Hilt's [@HiltViewModel] and survives configuration changes automatically.
 *
 * Responsibilities:
 *  - Exposes [uiState] as an immutable [StateFlow] for the Fragment to observe.
 *  - Delegates data fetching to [GetNewsFeedUseCase].
 *  - Handles bookmark toggle via [BookmarkArticleUseCase].
 */
@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsFeedUiState>(NewsFeedUiState.Idle)
    val uiState: StateFlow<NewsFeedUiState> = _uiState.asStateFlow()

    fun loadFeed(category: NewsCategory) {
        viewModelScope.launch {
            getNewsFeedUseCase(category).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading    -> NewsFeedUiState.Loading
                    is NetworkResult.Success    -> {
                        if (result.data.isEmpty()) NewsFeedUiState.Empty
                        else NewsFeedUiState.Success(result.data)
                    }
                    is NetworkResult.Error      -> NewsFeedUiState.Error(result.message)
                }
            }
        }
    }

    fun toggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            bookmarkArticleUseCase(article)
        }
    }
}
