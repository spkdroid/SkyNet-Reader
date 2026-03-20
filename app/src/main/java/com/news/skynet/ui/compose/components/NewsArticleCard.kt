package com.news.skynet.ui.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.ui.compose.theme.CatBusiness
import com.news.skynet.ui.compose.theme.CatEntertainment
import com.news.skynet.ui.compose.theme.CatPolitics
import com.news.skynet.ui.compose.theme.CatTechnology
import com.news.skynet.ui.compose.theme.CatWorld
import com.news.skynet.domain.model.NewsCategory

/**
 * Reusable news-article card used across Feed, Search, and Bookmarks screens.
 */
@Composable
fun NewsArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    onBookmarkToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape     = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Hero image
            if (article.imageUrl.isNotBlank()) {
                AsyncImage(
                    model             = article.imageUrl,
                    contentDescription = article.title,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(Modifier.height(10.dp))
            }

            // Category chip + bookmark toggle
            Row(
                modifier       = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AssistChip(
                    onClick = {},
                    label   = {
                        Text(
                            text  = article.category.displayName,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )

                if (onBookmarkToggle != null) {
                    IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector       = if (article.isBookmarked) Icons.Filled.Bookmark
                                                else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (article.isBookmarked) "Remove bookmark"
                                                 else "Add bookmark",
                            tint              = if (article.isBookmarked)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Title
            Text(
                text     = article.title,
                style    = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Summary
            if (article.summary.isNotBlank()) {
                Text(
                    text     = article.summary,
                    style    = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
            }

            // Published date
            Text(
                text  = article.publishedAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Maps [NewsCategory] to an accent colour for visual differentiation. */
fun NewsCategory.accentColor() = when (this) {
    NewsCategory.WORLD         -> CatWorld
    NewsCategory.ENTERTAINMENT -> CatEntertainment
    NewsCategory.BUSINESS      -> CatBusiness
    NewsCategory.TECHNOLOGY    -> CatTechnology
    NewsCategory.POLITICS      -> CatPolitics
}
