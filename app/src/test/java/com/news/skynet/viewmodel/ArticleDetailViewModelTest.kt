package com.news.skynet.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.ui.detail.ArticleDetailViewModel
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val bookmarkUseCase: BookmarkArticleUseCase = mockk()
    private lateinit var viewModel: ArticleDetailViewModel

    private val sampleArticle = NewsArticle(
        id = "1", title = "Test Article", summary = "Summary text for testing",
        url = "https://example.com", imageUrl = "https://img.com/1",
        publishedAt = "2026-01-01", category = NewsCategory.TECHNOLOGY
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ArticleDetailViewModel(bookmarkUseCase)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init sets bookmark state from article`() {
        viewModel.init(sampleArticle)
        assertFalse(viewModel.isBookmarked.value!!)
    }

    @Test
    fun `init with bookmarked article sets true`() {
        val bookmarked = sampleArticle.copy(isBookmarked = true)
        viewModel.init(bookmarked)
        assertTrue(viewModel.isBookmarked.value!!)
    }

    @Test
    fun `toggleBookmark flips state and calls use case`() = runTest {
        coJustRun { bookmarkUseCase(any()) }
        viewModel.init(sampleArticle)
        assertFalse(viewModel.isBookmarked.value!!)

        viewModel.toggleBookmark()
        advanceUntilIdle()

        assertTrue(viewModel.isBookmarked.value!!)
        coVerify(exactly = 1) { bookmarkUseCase(sampleArticle) }
    }

    @Test
    fun `toggleBookmark twice returns to original state`() = runTest {
        coJustRun { bookmarkUseCase(any()) }
        viewModel.init(sampleArticle)

        viewModel.toggleBookmark()
        advanceUntilIdle()
        assertTrue(viewModel.isBookmarked.value!!)

        viewModel.toggleBookmark()
        advanceUntilIdle()
        assertFalse(viewModel.isBookmarked.value!!)
    }

    @Test
    fun `estimatedReadingTime returns 1 min for short text`() {
        val result = viewModel.estimatedReadingTime("Hello world")
        assertEquals("1 min read", result)
    }

    @Test
    fun `estimatedReadingTime calculates correctly for longer text`() {
        // 400 words → 2 min read at 200 wpm
        val text = (1..400).joinToString(" ") { "word" }
        val result = viewModel.estimatedReadingTime(text)
        assertEquals("2 min read", result)
    }

    @Test
    fun `estimatedReadingTime handles 1000 words`() {
        val text = (1..1000).joinToString(" ") { "word" }
        val result = viewModel.estimatedReadingTime(text)
        assertEquals("5 min read", result)
    }
}
