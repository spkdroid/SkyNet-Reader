package com.news.skynet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.news.skynet.ui.compose.screens.OnboardingScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

@HiltAndroidTest
class OnboardingScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_showsFirstPage() {
        composeTestRule.setContent {
            OnboardingScreen(onFinished = {})
        }

        composeTestRule
            .onNodeWithText("Welcome to SkyNet Reader")
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_showsSkipButton() {
        composeTestRule.setContent {
            OnboardingScreen(onFinished = {})
        }

        composeTestRule
            .onNodeWithText("Skip")
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_showsNextButton() {
        composeTestRule.setContent {
            OnboardingScreen(onFinished = {})
        }

        composeTestRule
            .onNodeWithText("Next")
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_skipButtonCallsOnFinished() {
        var finished = false
        composeTestRule.setContent {
            OnboardingScreen(onFinished = { finished = true })
        }

        composeTestRule
            .onNodeWithText("Skip")
            .performClick()

        assertTrue(finished)
    }
}
