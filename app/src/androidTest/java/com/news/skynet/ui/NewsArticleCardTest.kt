package com.news.skynet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.ui.compose.components.NewsArticleCard
import com.news.skynet.ui.compose.theme.SkyNetTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NewsArticleCardTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val sampleArticle = NewsArticle(
        id = "1",
        title = "Breaking News: Kotlin 2.0 Released",
        summary = "The new version brings major performance improvements",
        url = "https://example.com",
        imageUrl = "",
        publishedAt = "2026-03-19",
        category = NewsCategory.TECHNOLOGY
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun cardDisplaysArticleTitle() {
        composeTestRule.setContent {
            SkyNetTheme {
                NewsArticleCard(article = sampleArticle, onClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("Breaking News: Kotlin 2.0 Released")
            .assertIsDisplayed()
    }

    @Test
    fun cardDisplaysArticleSummary() {
        composeTestRule.setContent {
            SkyNetTheme {
                NewsArticleCard(article = sampleArticle, onClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("The new version brings major performance improvements")
            .assertIsDisplayed()
    }

    @Test
    fun cardDisplaysCategoryChip() {
        composeTestRule.setContent {
            SkyNetTheme {
                NewsArticleCard(article = sampleArticle, onClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("Technology")
            .assertIsDisplayed()
    }

    @Test
    fun cardClickTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            SkyNetTheme {
                NewsArticleCard(
                    article = sampleArticle,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Breaking News: Kotlin 2.0 Released")
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun cardShowsAllCategoryTypes() {
        for (category in NewsCategory.values()) {
            val article = sampleArticle.copy(category = category)
            composeTestRule.setContent {
                SkyNetTheme {
                    NewsArticleCard(article = article, onClick = {})
                }
            }

            composeTestRule
                .onNodeWithText(category.displayName)
                .assertIsDisplayed()
        }
    }
}
