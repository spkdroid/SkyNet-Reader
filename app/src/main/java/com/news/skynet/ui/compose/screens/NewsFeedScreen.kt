package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.ui.compose.components.NewsArticleCard
import com.news.skynet.ui.feed.NewsFeedUiState
import com.news.skynet.ui.feed.NewsFeedViewModel
import kotlinx.coroutines.launch

private val categories = NewsCategory.values().toList()

/**
 * News feed screen with a scrollable category tab row + horizontal pager.
 * Each page owns its own [NewsFeedViewModel] instance (Hilt key = category index).
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NewsFeedScreen(onArticleClick: (NewsArticle) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val scope      = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Category tabs ──────────────────────────────────────────────────
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding      = 0.dp
        ) {
            categories.forEachIndexed { index, cat ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick  = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text     = { Text(cat.displayName) }
                )
            }
        }

        // ── Pager ──────────────────────────────────────────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            NewsFeedPage(
                category     = categories[page],
                onArticleClick = onArticleClick
            )
        }
    }
}

/** A single category page with its own scoped ViewModel. */
@Composable
private fun NewsFeedPage(
    category: NewsCategory,
    onArticleClick: (NewsArticle) -> Unit,
    viewModel: NewsFeedViewModel = hiltViewModel()
) {
    // Load feed for this category once
    LaunchedEffect(category) { viewModel.loadFeed(category) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val s = state) {
            is NewsFeedUiState.Loading, NewsFeedUiState.Idle -> {
                CircularProgressIndicator()
            }

            is NewsFeedUiState.Success -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.articles, key = { it.id }) { article ->
                        NewsArticleCard(
                            article          = article,
                            onClick          = { onArticleClick(article) },
                            onBookmarkToggle = { viewModel.toggleBookmark(article) }
                        )
                    }
                }
            }

            is NewsFeedUiState.Empty -> {
                Text(
                    "No articles yet. Pull to refresh.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is NewsFeedUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Couldn't load news: ${s.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    TextButton(onClick = { viewModel.loadFeed(category) }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
