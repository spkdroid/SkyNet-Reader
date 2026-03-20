package com.news.skynet.domain.usecase

import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * SearchNewsUseCase.kt
 *
 * Searches for articles matching [query] across both the local Room cache
 * and the remote API. Returns a Flow of NetworkResult so the UI can show
 * loading / success / error states.
 */
class SearchNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(query: String): Flow<NetworkResult<List<NewsArticle>>> =
        repository.searchArticles(query)
}
