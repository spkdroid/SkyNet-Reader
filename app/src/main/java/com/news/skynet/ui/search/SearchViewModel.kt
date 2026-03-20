package com.news.skynet.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.usecase.SearchNewsUseCase
import com.news.skynet.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SearchViewModel.kt
 *
 * Drives the Search screen. Uses [debounce] to avoid firing a network/DB
 * query on every keystroke — only triggers after 300 ms of inactivity.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        observeQuery()
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .flatMapLatest { query -> searchNewsUseCase(query) }
                .collectLatest { result ->
                    _uiState.value = when (result) {
                        is NetworkResult.Loading -> SearchUiState.Loading
                        is NetworkResult.Success -> {
                            if (result.data.isEmpty()) SearchUiState.Empty
                            else SearchUiState.Success(result.data)
                        }
                        is NetworkResult.Error   -> SearchUiState.Error(result.message)
                    }
                }
        }
    }
}

sealed class SearchUiState {
    object Idle    : SearchUiState()
    object Loading : SearchUiState()
    object Empty   : SearchUiState()
    data class Success(val articles: List<NewsArticle>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
