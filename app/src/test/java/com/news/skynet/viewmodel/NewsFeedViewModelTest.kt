package com.news.skynet.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.domain.usecase.GetNewsFeedUseCase
import com.news.skynet.ui.feed.NewsFeedUiState
import com.news.skynet.ui.feed.NewsFeedViewModel
import com.news.skynet.util.NetworkResult
import io.mockk.coJustRun
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * NewsFeedViewModelTest.kt
 *
 * Tests that the ViewModel correctly maps [NetworkResult] from the use case
 * to [NewsFeedUiState] transitions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val getFeedUseCase: GetNewsFeedUseCase = mockk()
    private val bookmarkUseCase: BookmarkArticleUseCase = mockk()
    private lateinit var viewModel: NewsFeedViewModel

    private val sampleArticles = listOf(
        NewsArticle(
            id = "1", title = "Headline", summary = "Details",
            url = "https://example.com", imageUrl = "",
            publishedAt = "2026-03-19", category = NewsCategory.WORLD
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NewsFeedViewModel(getFeedUseCase, bookmarkUseCase)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFeed emits Success state on successful fetch`() = runTest {
        every { getFeedUseCase(NewsCategory.WORLD) } returns
            flowOf(NetworkResult.Loading, NetworkResult.Success(sampleArticles))

        viewModel.uiState.test {
            awaitItem() // Idle
            viewModel.loadFeed(NewsCategory.WORLD)
            assertTrue(awaitItem() is NewsFeedUiState.Loading)
            val success = awaitItem()
            assertTrue(success is NewsFeedUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadFeed emits Empty state when article list is empty`() = runTest {
        every { getFeedUseCase(NewsCategory.BUSINESS) } returns
            flowOf(NetworkResult.Success(emptyList()))

        viewModel.uiState.test {
            awaitItem() // Idle
            viewModel.loadFeed(NewsCategory.BUSINESS)
            assertTrue(awaitItem() is NewsFeedUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadFeed emits Error state on network failure`() = runTest {
        every { getFeedUseCase(NewsCategory.POLITICS) } returns
            flowOf(NetworkResult.Error("Timeout"))

        viewModel.uiState.test {
            awaitItem() // Idle
            viewModel.loadFeed(NewsCategory.POLITICS)
            val error = awaitItem()
            assertTrue(error is NewsFeedUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
