package com.dija.skynetreader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dija.skynetreader.domain.model.NewsArticle
import com.dija.skynetreader.domain.usecase.GetNewsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsByCategory: GetNewsByCategoryUseCase
) : ViewModel() {

    init {
        loadNews(1)
    }

    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>> = _articles

    fun loadNews(type: Int) {
        viewModelScope.launch {
            getNewsByCategory(type).collect {
                _articles.value = it
            }
        }
    }
}