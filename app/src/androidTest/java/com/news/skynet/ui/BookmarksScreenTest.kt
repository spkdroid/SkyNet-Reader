package com.news.skynet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.news.skynet.ui.bookmarks.BookmarksUiState
import com.news.skynet.ui.compose.screens.BookmarksScreen
import com.news.skynet.ui.compose.theme.SkyNetTheme
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.domain.usecase.BookmarkArticleUseCase
import com.news.skynet.domain.usecase.GetBookmarksUseCase
import com.news.skynet.ui.bookmarks.BookmarksViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BookmarksScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val sampleArticles = listOf(
        NewsArticle(
            id = "1", title = "Bookmarked Article One",
            summary = "First summary", url = "https://example.com/1",
            imageUrl = "", publishedAt = "2026-01-01",
            category = NewsCategory.WORLD, isBookmarked = true
        ),
        NewsArticle(
            id = "2", title = "Bookmarked Article Two",
            summary = "Second summary", url = "https://example.com/2",
            imageUrl = "", publishedAt = "2026-01-02",
            category = NewsCategory.TECHNOLOGY, isBookmarked = true
        )
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun bookmarksScreen_showsEmptyState() {
        val getBookmarksUseCase: GetBookmarksUseCase = mockk()
        val bookmarkArticleUseCase: BookmarkArticleUseCase = mockk()
        every { getBookmarksUseCase() } returns flowOf(emptyList())

        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        composeTestRule.setContent {
            SkyNetTheme {
                BookmarksScreen(viewModel = viewModel, onArticleClick = {})
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("No bookmarks yet")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarksScreen_showsBookmarkedArticles() {
        val getBookmarksUseCase: GetBookmarksUseCase = mockk()
        val bookmarkArticleUseCase: BookmarkArticleUseCase = mockk()
        every { getBookmarksUseCase() } returns flowOf(sampleArticles)
        coJustRun { bookmarkArticleUseCase(any()) }

        val viewModel = BookmarksViewModel(getBookmarksUseCase, bookmarkArticleUseCase)

        composeTestRule.setContent {
            SkyNetTheme {
                BookmarksScreen(viewModel = viewModel, onArticleClick = {})
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Bookmarked Article One")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Bookmarked Article Two")
            .assertIsDisplayed()
    }
}
