package com.news.skynet.repository

import app.cash.turbine.test
import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.BookmarkEntity
import com.news.skynet.data.repository.BookmarkRepository
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
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
class BookmarkRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bookmarkDao: BookmarkDao = mockk(relaxed = true)
    private lateinit var repository: BookmarkRepository

    private val sampleEntity = BookmarkEntity(
        id = "1", title = "Saved", summary = "Summary",
        url = "https://example.com", imageUrl = "", publishedAt = "2026-03-19",
        categoryType = 1, savedAt = System.currentTimeMillis()
    )

    private val sampleArticle = NewsArticle(
        id = "1", title = "Saved", summary = "Summary",
        url = "https://example.com", imageUrl = "", publishedAt = "2026-03-19",
        category = NewsCategory.WORLD
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = BookmarkRepository(bookmarkDao)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllBookmarks returns mapped domain articles with isBookmarked true`() = runTest {
        every { bookmarkDao.getAllBookmarks() } returns flowOf(listOf(sampleEntity))

        repository.getAllBookmarks().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertTrue(result.first().isBookmarked)
            assertEquals("Saved", result.first().title)
            awaitComplete()
        }
    }

    @Test
    fun `getAllBookmarks returns empty list when no bookmarks`() = runTest {
        every { bookmarkDao.getAllBookmarks() } returns flowOf(emptyList())

        repository.getAllBookmarks().test {
            assertEquals(emptyList<NewsArticle>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isBookmarked delegates to dao`() = runTest {
        every { bookmarkDao.isBookmarked("1") } returns flowOf(true)

        repository.isBookmarked("1").test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `toggleBookmark inserts entity into dao`() = runTest {
        every { bookmarkDao.isBookmarked("1") } returns flowOf(false)
        coEvery { bookmarkDao.insert(any()) } returns Unit

        repository.toggleBookmark(sampleArticle)

        coVerify { bookmarkDao.insert(any()) }
    }

    @Test
    fun `removeBookmark calls deleteById on dao`() = runTest {
        coEvery { bookmarkDao.deleteById("1") } returns Unit

        repository.removeBookmark("1")

        coVerify { bookmarkDao.deleteById("1") }
    }
}
