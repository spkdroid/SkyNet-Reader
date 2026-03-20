package com.news.skynet.ui.detail

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ArticleDetailViewModel.kt
 *
 * Holds UI state for [ArticleDetailFragment]. Manages bookmark toggling
 * and exposes a [LiveData] of the current bookmark state.
 */
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    private lateinit var currentArticle: NewsArticle

    private val _isBookmarked = MutableLiveData(false)
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    fun init(article: NewsArticle) {
        currentArticle = article
        _isBookmarked.value = article.isBookmarked
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            bookmarkArticleUseCase(currentArticle)
            _isBookmarked.value = _isBookmarked.value?.not() ?: true
        }
    }

    fun shareArticle(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, currentArticle.title)
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(Intent.createChooser(intent, "Share article via"))
    }

    /** Returns a human-readable reading time estimate based on word count. */
    fun estimatedReadingTime(text: String): String {
        val wordCount = text.trim().split("\\s+".toRegex()).size
        val minutes   = maxOf(1, wordCount / 200) // average reading speed: 200 wpm
        return "$minutes min read"
    }
}
