package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.news.skynet.ui.compose.theme.OnboardBg1
import com.news.skynet.ui.compose.theme.OnboardBg2
import com.news.skynet.ui.compose.theme.OnboardBg3
import com.news.skynet.ui.compose.theme.OnboardBg4
import kotlinx.coroutines.launch

private data class OnboardPage(
    val icon: ImageVector,
    val title: String,
    val body: String,
    val background: Color
)

private val pages = listOf(
    OnboardPage(
        icon       = Icons.Filled.RocketLaunch,
        title      = "Welcome to SkyNet Reader",
        body       = "Your all-in-one news hub. Stay updated with the latest world, technology, business, and politics headlines — curated just for you.",
        background = OnboardBg1
    ),
    OnboardPage(
        icon       = Icons.Filled.Bookmark,
        title      = "Save What Matters",
        body       = "Bookmark articles to read offline later. Everything is stored locally on your device — no account required.",
        background = OnboardBg2
    ),
    OnboardPage(
        icon       = Icons.Filled.Notifications,
        title      = "Never Miss a Story",
        body       = "Opt in to breaking-news notifications and let SkyNet refresh headlines in the background so you're always in the loop.",
        background = OnboardBg3
    ),
    OnboardPage(
        icon       = Icons.Filled.AutoAwesome,
        title      = "AI-Powered Insights",
        body       = "Ask questions, request summaries, or explore topics with the on-device Gemma AI assistant — completely private, runs offline.",
        background = OnboardBg4
    )
)

/**
 * Full-screen onboarding flow built in pure Compose using [HorizontalPager].
 * Calls [onFinished] when the user taps "Get Started" on the last page
 * or "Skip" from any page.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    HorizontalPager(
        state    = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        val page = pages[pageIndex]
        OnboardPageContent(page = page)
    }

    // Overlay controls (skip, dots, next/done)
    Box(modifier = Modifier.fillMaxSize()) {
        // Skip button — top-right
        if (!isLastPage) {
            TextButton(
                onClick  = onFinished,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 8.dp)
            ) {
                Text("Skip", color = Color.White.copy(alpha = 0.8f))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicator dots
            DotsIndicator(
                totalDots  = pages.size,
                currentDot = pagerState.currentPage
            )

            Spacer(Modifier.height(24.dp))

            // Next / Get Started button
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinished()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text  = if (isLastPage) "Get Started" else "Next",
                    color = pages[pagerState.currentPage].background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OnboardPageContent(page: OnboardPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(page.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector       = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint     = Color.White
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text       = page.title,
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text      = page.body,
                style     = MaterialTheme.typography.bodyLarge,
                color     = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DotsIndicator(totalDots: Int, currentDot: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentDot) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentDot) Color.White
                        else Color.White.copy(alpha = 0.4f)
                    )
            )
        }
    }
}
