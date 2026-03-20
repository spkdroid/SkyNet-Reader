package com.news.skynet.usecase

import app.cash.turbine.test
import com.news.skynet.data.repository.BookmarkRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.GetBookmarksUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetBookmarksUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookmarkRepository = mockk()
    private lateinit var useCase: GetBookmarksUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetBookmarksUseCase(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns bookmarks from repository`() = runTest {
        val bookmarks = listOf(
            NewsArticle(
                id = "1", title = "Saved", summary = "Summary",
                url = "https://example.com", imageUrl = "", publishedAt = "2026-01-01",
                category = NewsCategory.WORLD, isBookmarked = true
            )
        )
        every { repository.getAllBookmarks() } returns flowOf(bookmarks)

        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Saved", result.first().title)
            assertTrue(result.first().isBookmarked)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when no bookmarks`() = runTest {
        every { repository.getAllBookmarks() } returns flowOf(emptyList())

        useCase().test {
            assertEquals(emptyList<NewsArticle>(), awaitItem())
            awaitComplete()
        }
    }
}
