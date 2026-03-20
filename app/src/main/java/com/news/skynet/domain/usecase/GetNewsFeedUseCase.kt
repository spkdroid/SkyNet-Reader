package com.news.skynet.domain.usecase

import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetNewsFeedUseCase.kt
 *
 * Single-responsibility use case that retrieves a paginated news feed for a
 * given category. The repository exposes a Flow so the caller (ViewModel) can
 * observe live updates from Room while Retrofit refreshes in the background.
 */
class GetNewsFeedUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(category: NewsCategory): Flow<NetworkResult<List<NewsArticle>>> =
        repository.getNewsFeed(category)
}
