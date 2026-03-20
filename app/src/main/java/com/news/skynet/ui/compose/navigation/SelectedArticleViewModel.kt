package com.news.skynet.ui.compose.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.news.skynet.domain.model.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Lightweight ViewModel scoped to the Activity that carries the article
 * the user tapped so [ArticleDetailScreen] can read it without serialisation.
 */
@HiltViewModel
class SelectedArticleViewModel @Inject constructor() : ViewModel() {
    var article: NewsArticle? by mutableStateOf(null)
        private set

    fun select(a: NewsArticle) { article = a }
}
