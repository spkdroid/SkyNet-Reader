package com.news.skynet.domain.usecase

import com.news.skynet.data.repository.BookmarkRepository
import com.news.skynet.domain.model.NewsArticle
import javax.inject.Inject

/**
 * BookmarkArticleUseCase.kt
 *
 * Toggles the bookmarked state of an article. If the article is already
 * bookmarked it is removed; otherwise it is saved to Room.
 */
class BookmarkArticleUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(article: NewsArticle) =
        repository.toggleBookmark(article)
}
