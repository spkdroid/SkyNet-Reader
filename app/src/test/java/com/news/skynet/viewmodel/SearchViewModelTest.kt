package com.news.skynet.viewmodel

import app.cash.turbine.test
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.SearchNewsUseCase
import com.news.skynet.ui.search.SearchUiState
import com.news.skynet.ui.search.SearchViewModel
import com.news.skynet.util.NetworkResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val searchUseCase: SearchNewsUseCase = mockk()
    private lateinit var viewModel: SearchViewModel

    private val sampleArticles = listOf(
        NewsArticle(
            id = "1", title = "Test Article", summary = "Details",
            url = "https://example.com", imageUrl = "",
            publishedAt = "2026-03-19", category = NewsCategory.TECHNOLOGY
        )
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
    fun `initial state is Idle`() = runTest {
        every { searchUseCase(any()) } returns flowOf(NetworkResult.Success(emptyList()))
        viewModel = SearchViewModel(searchUseCase)

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `query with less than 2 chars does not trigger search`() = runTest {
        every { searchUseCase(any()) } returns flowOf(NetworkResult.Success(sampleArticles))
        viewModel = SearchViewModel(searchUseCase)

        viewModel.onQueryChanged("a")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `query with 2+ chars triggers search after debounce`() = runTest {
        every { searchUseCase("test") } returns
            flowOf(NetworkResult.Success(sampleArticles))
        viewModel = SearchViewModel(searchUseCase)

        viewModel.onQueryChanged("test")
        advanceTimeBy(400)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success but got $state", state is SearchUiState.Success)
        assertEquals(1, (state as SearchUiState.Success).articles.size)
    }

    @Test
    fun `empty result returns Empty state`() = runTest {
        every { searchUseCase("empty") } returns
            flowOf(NetworkResult.Success(emptyList()))
        viewModel = SearchViewModel(searchUseCase)

        viewModel.onQueryChanged("empty")
        advanceTimeBy(400)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Empty)
    }

    @Test
    fun `error result returns Error state`() = runTest {
        every { searchUseCase("fail") } returns
            flowOf(NetworkResult.Error("Network error"))
        viewModel = SearchViewModel(searchUseCase)

        viewModel.onQueryChanged("fail")
        advanceTimeBy(400)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
        assertEquals("Network error", (state as SearchUiState.Error).message)
    }
}
