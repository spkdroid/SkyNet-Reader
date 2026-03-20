package com.news.skynet.domain.usecase

import com.news.skynet.data.repository.BookmarkRepository
import com.news.skynet.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetBookmarksUseCase.kt
 *
 * Returns a continuously-updating Flow of all bookmarked articles from Room.
 */
class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<NewsArticle>> =
        repository.getAllBookmarks()
}
