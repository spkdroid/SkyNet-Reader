package com.news.skynet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.news.skynet.ui.compose.AppViewModel
import com.news.skynet.ui.compose.navigation.SkyNetNavGraph
import com.news.skynet.ui.compose.theme.SkyNetTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity host for the entire SkyNet Reader application.
 *
 * All navigation is driven by Jetpack Compose Navigation (no Fragments).
 * The Activity only decides:
 *  1. Whether to show onboarding (based on DataStore flag)
 *  2. Which colour theme to apply (dark / light / dynamic)
 *
 * Everything else is handled inside [SkyNetNavGraph].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appVm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Wait for DataStore to emit — null means "not yet read from disk"
            val onboardingComplete by appVm.onboardingComplete.collectAsStateWithLifecycle()
            val isDark             by appVm.isDarkMode.collectAsStateWithLifecycle()

            // Don't render until the initial DataStore read completes to avoid
            // a flash to the wrong start destination
            if (onboardingComplete == null) return@setContent

            SkyNetTheme(darkTheme = isDark) {
                SkyNetNavGraph(
                    startOnboarding      = onboardingComplete == false,
                    onOnboardingComplete = { appVm.completeOnboarding() }
                )
            }
        }
    }
}

