package com.news.skynet.data.remote

import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NewsRemoteDataSource.kt
 *
 * Wraps all Retrofit calls, catches exceptions, and converts [NewsDto] objects
 * into the domain [NewsArticle] model. This is the only place in the app that
 * talks directly to the Retrofit service.
 */
@Singleton
class NewsRemoteDataSource @Inject constructor(
    private val api: NewsApiService
) {
    suspend fun getNewsFeed(category: NewsCategory): NetworkResult<List<NewsArticle>> =
        safeApiCall { api.getNewsFeed(category.apiType).map { it.toDomain(category) } }

    suspend fun searchArticles(query: String): NetworkResult<List<NewsArticle>> =
        safeApiCall { api.searchArticles(query).map { it.toDomainUnknownCategory() } }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private inline fun <T> safeApiCall(call: () -> T): NetworkResult<T> =
        try {
            NetworkResult.Success(call())
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Error(
                message = e.message() ?: "HTTP error",
                code = e.code()
            )
        } catch (e: Exception) {
            NetworkResult.Error(message = e.localizedMessage ?: "Unknown error")
        }

    private fun NewsDto.toDomain(category: NewsCategory) = NewsArticle(
        id          = id ?: url.orEmpty(),
        title       = title.orEmpty(),
        summary     = newsLine.orEmpty(),
        url         = url.orEmpty(),
        imageUrl    = imageUrl.orEmpty(),
        publishedAt = date.orEmpty(),
        category    = category
    )

    private fun NewsDto.toDomainUnknownCategory() = NewsArticle(
        id          = id ?: url.orEmpty(),
        title       = title.orEmpty(),
        summary     = newsLine.orEmpty(),
        url         = url.orEmpty(),
        imageUrl    = imageUrl.orEmpty(),
        publishedAt = date.orEmpty(),
        category    = NewsCategory.fromApiType(type ?: 1)
    )
}
