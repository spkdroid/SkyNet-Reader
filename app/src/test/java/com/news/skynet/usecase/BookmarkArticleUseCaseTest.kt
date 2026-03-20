package com.news.skynet.usecase

import app.cash.turbine.test
import com.news.skynet.data.repository.BookmarkRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkArticleUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookmarkRepository = mockk()
    private lateinit var useCase: BookmarkArticleUseCase

    private val article = NewsArticle(
        id = "1", title = "Test", summary = "Summary",
        url = "https://example.com", imageUrl = "", publishedAt = "2026-03-19",
        category = NewsCategory.WORLD
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = BookmarkArticleUseCase(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke delegates to repository toggleBookmark`() = runTest {
        coEvery { repository.toggleBookmark(article) } returns Unit

        useCase(article)

        coVerify(exactly = 1) { repository.toggleBookmark(article) }
    }
}
