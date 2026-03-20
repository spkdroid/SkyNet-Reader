package com.news.skynet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.news.skynet.ui.compose.screens.SettingsScreen
import com.news.skynet.ui.compose.theme.SkyNetTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun settingsScreen_showsAppearanceSection() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Appearance")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsDarkModeToggle() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Dark Mode")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsReadingSection() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Reading")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsNotificationsSection() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Notifications")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsBreakingNewsToggle() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Breaking News Alerts")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsAboutSection() {
        composeTestRule.setContent {
            SkyNetTheme {
                SettingsScreen()
            }
        }

        composeTestRule
            .onNodeWithText("About")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("SkyNet Reader")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Version 2.0")
            .assertIsDisplayed()
    }
}
