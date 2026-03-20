package com.news.skynet.data.repository

import com.news.skynet.data.local.NewsDao
import com.news.skynet.data.local.NewsEntity
import com.news.skynet.data.remote.NewsRemoteDataSource
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours

/**
 * NewsRepository.kt
 *
 * Single source of truth for news articles. Implements an offline-first
 * strategy:
 *   1. Immediately emits locally cached articles from Room (if any).
 *   2. Fetches fresh data from the API and inserts it into Room.
 *   3. Room notifies collectors via its Flow — the UI updates automatically.
 *
 * Errors during network fetches are wrapped in [NetworkResult.Error] so the
 * UI can surface them without crashing.
 */
@Singleton
class NewsRepository @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    private val newsDao: NewsDao
) {
    fun getNewsFeed(category: NewsCategory): Flow<NetworkResult<List<NewsArticle>>> = flow {
        emit(NetworkResult.Loading)

        // 1. Emit whatever is in the local cache immediately.
        val cached = newsDao.getArticlesByCategory(category.apiType)
            .map { entities -> NetworkResult.Success(entities.toDomainList()) as NetworkResult<List<NewsArticle>> }

        // 2. Fetch from network and update Room.
        val networkResult = remoteDataSource.getNewsFeed(category)
        if (networkResult is NetworkResult.Success) {
            newsDao.deleteByCategory(category.apiType)
            newsDao.insertArticles(networkResult.data.toEntityList())
        } else if (networkResult is NetworkResult.Error) {
            emit(networkResult)
        }

        // 3. Emit live Room stream (includes freshly inserted rows).
        emitAll(cached)
    }.catch { e ->
        emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
    }

    fun searchArticles(query: String): Flow<NetworkResult<List<NewsArticle>>> =
        newsDao.searchArticles(query)
            .map { entities -> NetworkResult.Success(entities.toDomainList()) as NetworkResult<List<NewsArticle>> }
            .onStart { emit(NetworkResult.Loading) }
            .catch { e -> emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error")) }

    // -------------------------------------------------------------------------
    // Entity ↔ Domain mappers
    // -------------------------------------------------------------------------

    private fun List<NewsEntity>.toDomainList() = map { entity ->
        NewsArticle(
            id          = entity.id,
            title       = entity.title,
            summary     = entity.summary,
            url         = entity.url,
            imageUrl    = entity.imageUrl,
            publishedAt = entity.publishedAt,
            category    = NewsCategory.fromApiType(entity.categoryType)
        )
    }

    private fun List<NewsArticle>.toEntityList() = map { article ->
        NewsEntity(
            id           = article.id,
            title        = article.title,
            summary      = article.summary,
            url          = article.url,
            imageUrl     = article.imageUrl,
            publishedAt  = article.publishedAt,
            categoryType = article.category.apiType
        )
    }
}
