package com.news.skynet.viewmodel

import app.cash.turbine.test
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.domain.usecase.GetBookmarksUseCase
import com.news.skynet.ui.bookmarks.BookmarksUiState
import com.news.skynet.ui.bookmarks.BookmarksViewModel
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getBookmarksUseCase: GetBookmarksUseCase = mockk()
    private val bookmarkArticleUseCase: BookmarkArticleUseCase = mockk()

    private val sampleArticle = NewsArticle(
        id = "1", title = "Bookmarked", summary = "Summary",
        url = "https://example.com", imageUrl = "",
        publishedAt = "2026-01-01", category = NewsCategory.WORLD,
        isBookmarked = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(listOf(sampleArticle))
        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        assertEquals(BookmarksUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Success when bookmarks exist`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(listOf(sampleArticle))
        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem()
            assertTrue("Expected Success but got $state", state is BookmarksUiState.Success)
            assertEquals(1, (state as BookmarksUiState.Success).bookmarks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Empty when no bookmarks`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(emptyList())
        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            assertTrue(awaitItem() is BookmarksUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeBookmark calls use case`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(listOf(sampleArticle))
        coJustRun { bookmarkArticleUseCase(any()) }
        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        viewModel.removeBookmark(sampleArticle)
        advanceUntilIdle()

        coVerify(exactly = 1) { bookmarkArticleUseCase(sampleArticle) }
    }
}
