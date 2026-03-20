package com.news.skynet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.news.skynet.ui.compose.screens.SearchScreen
import com.news.skynet.ui.compose.theme.SkyNetTheme
import com.news.skynet.domain.usecase.SearchNewsUseCase
import com.news.skynet.ui.search.SearchViewModel
import com.news.skynet.util.NetworkResult
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchScreen_showsIdleHint() {
        val searchUseCase: SearchNewsUseCase = mockk()
        every { searchUseCase(any()) } returns flowOf(NetworkResult.Success(emptyList()))
        val viewModel = SearchViewModel(searchUseCase)

        composeTestRule.setContent {
            SkyNetTheme {
                SearchScreen(viewModel = viewModel, onArticleClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("Type at least 2 characters to search")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_showsSearchBar() {
        val searchUseCase: SearchNewsUseCase = mockk()
        every { searchUseCase(any()) } returns flowOf(NetworkResult.Success(emptyList()))
        val viewModel = SearchViewModel(searchUseCase)

        composeTestRule.setContent {
            SkyNetTheme {
                SearchScreen(viewModel = viewModel, onArticleClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("Search articles…")
            .assertIsDisplayed()
    }
}
