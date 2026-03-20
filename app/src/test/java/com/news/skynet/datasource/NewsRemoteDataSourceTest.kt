package com.news.skynet.datasource

import com.news.skynet.data.remote.NewsApiService
import com.news.skynet.data.remote.NewsDto
import com.news.skynet.data.remote.NewsRemoteDataSource
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class NewsRemoteDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val api: NewsApiService = mockk()
    private lateinit var dataSource: NewsRemoteDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataSource = NewsRemoteDataSource(api)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNewsFeed returns Success with mapped articles`() = runTest {
        val dtos = listOf(
            NewsDto(
                title = "Headline", newsLine = "Description",
                url = "https://example.com/1", imageUrl = "https://img.com/1.jpg",
                date = "Thu, 19 Mar 2026 11:00:00 +0000"
            )
        )
        coEvery { api.getNewsFeed(1) } returns dtos

        val result = dataSource.getNewsFeed(NewsCategory.WORLD)

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.size)
        assertEquals("Headline", data.first().title)
        assertEquals("Description", data.first().summary)
        assertEquals(NewsCategory.WORLD, data.first().category)
    }

    @Test
    fun `getNewsFeed filters out blank titles (empty trailing objects)`() = runTest {
        val dtos = listOf(
            NewsDto(title = "Valid", newsLine = "Description", url = "https://example.com", imageUrl = "", date = ""),
            NewsDto(title = null, newsLine = null, url = null, imageUrl = null, date = null),
            NewsDto(title = "", newsLine = "", url = "", imageUrl = "", date = "")
        )
        coEvery { api.getNewsFeed(2) } returns dtos

        val result = dataSource.getNewsFeed(NewsCategory.ENTERTAINMENT)

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun `getNewsFeed returns Error on HTTP exception`() = runTest {
        coEvery { api.getNewsFeed(1) } throws retrofit2.HttpException(
            okhttp3.ResponseBody.create(null, "").let {
                retrofit2.Response.error<Any>(500, it)
            }
        )

        val result = dataSource.getNewsFeed(NewsCategory.WORLD)

        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `getNewsFeed returns Error on generic exception`() = runTest {
        coEvery { api.getNewsFeed(1) } throws RuntimeException("Timeout")

        val result = dataSource.getNewsFeed(NewsCategory.WORLD)

        assertTrue(result is NetworkResult.Error)
        assertEquals("Timeout", (result as NetworkResult.Error).message)
    }

    @Test
    fun `searchArticles returns Success with mapped articles`() = runTest {
        val dtos = listOf(
            NewsDto(title = "Found", newsLine = "Result", url = "https://example.com/s", imageUrl = "", date = "")
        )
        coEvery { api.searchArticles("test") } returns dtos

        val result = dataSource.searchArticles("test")

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.size)
        assertEquals("Found", (result as NetworkResult.Success).data.first().title)
    }

    @Test
    fun `searchArticles returns Error on exception`() = runTest {
        coEvery { api.searchArticles("fail") } throws RuntimeException("Network error")

        val result = dataSource.searchArticles("fail")

        assertTrue(result is NetworkResult.Error)
    }
}
