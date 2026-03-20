package com.news.skynet.ui.compose.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.ui.detail.ArticleDetailViewModel

@Composable
fun ArticleDetailScreen(
    article: NewsArticle,
    onNavigateBack: () -> Unit,
    viewModel: ArticleDetailViewModel = hiltViewModel()
) {
    val context      = LocalContext.current
    val isBookmarked by viewModel.isBookmarked.observeAsState(article.isBookmarked)

    LaunchedEffect(article) { viewModel.init(article) }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { viewModel.toggleBookmark() }
                ) {
                    Icon(
                        imageVector       = if (isBookmarked) Icons.Filled.Bookmark
                                            else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark"
                    )
                }
                Spacer(Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    onClick = { viewModel.shareArticle(context, article.url) },
                    icon    = { Icon(Icons.Filled.Share, contentDescription = null) },
                    text    = { Text("Share") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero image
            if (article.imageUrl.isNotBlank()) {
                AsyncImage(
                    model             = article.imageUrl,
                    contentDescription = article.title,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(MaterialTheme.shapes.large)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Category + reading time
                Text(
                    "${article.category.displayName}  ·  ${viewModel.estimatedReadingTime(article.summary)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                // Title
                Text(
                    article.title,
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                // Published date
                Text(
                    article.publishedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Summary / body
                Text(
                    article.summary,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(80.dp))   // FAB clearance
            }
        }
    }
}
