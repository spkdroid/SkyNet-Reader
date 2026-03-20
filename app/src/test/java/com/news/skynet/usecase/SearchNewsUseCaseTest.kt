package com.news.skynet.usecase

import app.cash.turbine.test
import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.SearchNewsUseCase
import com.news.skynet.util.NetworkResult
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
class SearchNewsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NewsRepository = mockk()
    private lateinit var useCase: SearchNewsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = SearchNewsUseCase(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns articles matching query`() = runTest {
        val articles = listOf(
            NewsArticle(
                id = "1", title = "Found Article", summary = "Match",
                url = "https://example.com", imageUrl = "", publishedAt = "2026-03-19",
                category = NewsCategory.WORLD
            )
        )
        every { repository.searchArticles("test") } returns
            flowOf(NetworkResult.Success(articles))

        useCase("test").test {
            val result = awaitItem()
            assertTrue(result is NetworkResult.Success)
            assertEquals("Found Article", (result as NetworkResult.Success).data.first().title)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error on failure`() = runTest {
        every { repository.searchArticles("fail") } returns
            flowOf(NetworkResult.Error("Search failed"))

        useCase("fail").test {
            val result = awaitItem()
            assertTrue(result is NetworkResult.Error)
            awaitComplete()
        }
    }
}
