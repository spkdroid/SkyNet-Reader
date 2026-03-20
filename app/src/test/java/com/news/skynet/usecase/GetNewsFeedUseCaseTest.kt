package com.news.skynet.usecase

import app.cash.turbine.test
import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.GetNewsFeedUseCase
import com.news.skynet.util.NetworkResult
import io.mockk.coEvery
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

/**
 * GetNewsFeedUseCaseTest.kt
 *
 * Unit tests for [GetNewsFeedUseCase].
 * The repository is mocked with MockK — no Android framework required.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetNewsFeedUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NewsRepository = mockk()
    private lateinit var useCase: GetNewsFeedUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetNewsFeedUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns success when repository emits articles`() = runTest {
        val articles = listOf(
            NewsArticle(
                id = "1", title = "Test Article", summary = "Summary",
                url = "https://example.com", imageUrl = "", publishedAt = "2026-03-19",
                category = NewsCategory.WORLD
            )
        )
        coEvery { repository.getNewsFeed(NewsCategory.WORLD) } returns
            flowOf(NetworkResult.Success(articles))

        useCase(NewsCategory.WORLD).test {
            val result = awaitItem()
            assertTrue(result is NetworkResult.Success)
            assertEquals(1, (result as NetworkResult.Success).data.size)
            assertEquals("Test Article", result.data.first().title)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getNewsFeed(NewsCategory.TECHNOLOGY) } returns
            flowOf(NetworkResult.Error("Network unavailable"))

        useCase(NewsCategory.TECHNOLOGY).test {
            val result = awaitItem()
            assertTrue(result is NetworkResult.Error)
            assertEquals("Network unavailable", (result as NetworkResult.Error).message)
            awaitComplete()
        }
    }
}
