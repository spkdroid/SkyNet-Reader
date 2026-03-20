package com.news.skynet.repository

import app.cash.turbine.test
import com.news.skynet.data.local.NewsDao
import com.news.skynet.data.local.NewsEntity
import com.news.skynet.data.remote.NewsRemoteDataSource
import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
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
class NewsRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val remoteDataSource: NewsRemoteDataSource = mockk()
    private val newsDao: NewsDao = mockk(relaxed = true)
    private lateinit var repository: NewsRepository

    private val sampleEntities = listOf(
        NewsEntity(
            id = "https://example.com/1", title = "Cached Article", summary = "Summary",
            url = "https://example.com/1", imageUrl = "", publishedAt = "2026-03-19",
            categoryType = 1, cachedAt = System.currentTimeMillis()
        )
    )

    private val sampleArticles = listOf(
        NewsArticle(
            id = "https://example.com/2", title = "Fresh Article", summary = "Fresh summary",
            url = "https://example.com/2", imageUrl = "", publishedAt = "2026-03-19",
            category = NewsCategory.WORLD
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = NewsRepository(remoteDataSource, newsDao)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNewsFeed emits Loading then cached articles on network success`() = runTest {
        every { newsDao.getArticlesByCategory(1) } returns flowOf(sampleEntities)
        coEvery { remoteDataSource.getNewsFeed(NewsCategory.WORLD) } returns
            NetworkResult.Success(sampleArticles)
        coEvery { newsDao.deleteByCategory(1) } returns Unit
        coEvery { newsDao.insertArticles(any()) } returns Unit

        repository.getNewsFeed(NewsCategory.WORLD).test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals("Cached Article", (success as NetworkResult.Success).data.first().title)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { newsDao.deleteByCategory(1) }
        coVerify { newsDao.insertArticles(any()) }
    }

    @Test
    fun `getNewsFeed emits error from network then cached articles`() = runTest {
        every { newsDao.getArticlesByCategory(3) } returns flowOf(sampleEntities)
        coEvery { remoteDataSource.getNewsFeed(NewsCategory.BUSINESS) } returns
            NetworkResult.Error("Network error")

        repository.getNewsFeed(NewsCategory.BUSINESS).test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            val error = awaitItem()
            assertTrue(error is NetworkResult.Error)
            assertEquals("Network error", (error as NetworkResult.Error).message)
            // Still emits cached items after error
            val cached = awaitItem()
            assertTrue(cached is NetworkResult.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNewsFeed catches exception and emits Error`() = runTest {
        every { newsDao.getArticlesByCategory(1) } returns flowOf(emptyList())
        coEvery { remoteDataSource.getNewsFeed(NewsCategory.WORLD) } throws RuntimeException("Crash")

        repository.getNewsFeed(NewsCategory.WORLD).test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            val error = awaitItem()
            assertTrue(error is NetworkResult.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchArticles emits Loading then cached results`() = runTest {
        every { newsDao.searchArticles("test") } returns flowOf(sampleEntities)

        repository.searchArticles("test").test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(1, (success as NetworkResult.Success).data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
