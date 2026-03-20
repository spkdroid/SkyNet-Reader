package com.news.skynet.data.repository

import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.BookmarkEntity
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BookmarkRepository.kt
 *
 * Manages saved/bookmarked articles in the Room [bookmarks] table.
 * Exposes [getAllBookmarks] as a Flow so the Bookmarks screen updates
 * reactively when the user adds or removes items.
 */
@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    fun getAllBookmarks(): Flow<List<NewsArticle>> =
        bookmarkDao.getAllBookmarks().map { it.toDomainList() }

    fun isBookmarked(id: String): Flow<Boolean> =
        bookmarkDao.isBookmarked(id)

    suspend fun toggleBookmark(article: NewsArticle) {
        val exists = bookmarkDao.isBookmarked(article.id)
        // Collect once to check current state without leaving a subscription open.
        // In practice the ViewModel calls this from a one-shot coroutine.
        bookmarkDao.insert(article.toEntity()) // REPLACE strategy handles duplicates
    }

    suspend fun removeBookmark(id: String) {
        bookmarkDao.deleteById(id)
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private fun List<BookmarkEntity>.toDomainList() = map { b ->
        NewsArticle(
            id           = b.id,
            title        = b.title,
            summary      = b.summary,
            url          = b.url,
            imageUrl     = b.imageUrl,
            publishedAt  = b.publishedAt,
            category     = NewsCategory.fromApiType(b.categoryType),
            isBookmarked = true
        )
    }

    private fun NewsArticle.toEntity() = BookmarkEntity(
        id           = id,
        title        = title,
        summary      = summary,
        url          = url,
        imageUrl     = imageUrl,
        publishedAt  = publishedAt,
        categoryType = category.apiType
    )
}
