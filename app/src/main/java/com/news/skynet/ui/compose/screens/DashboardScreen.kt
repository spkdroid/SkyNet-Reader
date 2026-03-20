package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.ui.compose.components.accentColor
import com.news.skynet.ui.dashboard.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onArticleClick: (NewsArticle) -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToBookmarks: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Greeting ───────────────────────────────────────────────────────
        item(key = "greeting") { GreetingSection() }

        // ── Featured article hero ──────────────────────────────────────────
        state.featuredArticle?.let { featured ->
            item(key = "featured_header") {
                SectionHeader("Featured", Modifier.padding(horizontal = 16.dp))
            }
            item(key = "featured") {
                FeaturedArticleCard(
                    article  = featured,
                    onClick  = { onArticleClick(featured) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ── Trending Now ───────────────────────────────────────────────────
        if (state.trendingArticles.isNotEmpty()) {
            item(key = "trending") {
                SectionHeader("Trending Now", Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.trendingArticles, key = { it.id }) { article ->
                        TrendingArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }

        // ── Browse Categories ──────────────────────────────────────────────
        if (state.categoryStats.isNotEmpty()) {
            item(key = "categories") {
                SectionHeader("Browse Categories", Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categoryStats) { stat ->
                        CategoryCard(
                            category     = stat.category,
                            articleCount = stat.articleCount,
                            onClick      = onNavigateToFeed
                        )
                    }
                }
            }
        }

        // ── AI Assistant ───────────────────────────────────────────────────
        item(key = "ai") {
            AiAssistantCard(
                onClick  = onNavigateToChat,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ── Latest Headlines ───────────────────────────────────────────────
        if (state.latestArticles.isNotEmpty()) {
            item(key = "latest_header") {
                SectionHeader("Latest Headlines", Modifier.padding(horizontal = 16.dp))
            }
            val headlines = state.latestArticles
                .drop(if (state.featuredArticle != null) 1 else 0)
                .take(6)
            items(headlines, key = { "hl_${it.id}" }) { article ->
                LatestHeadlineCard(
                    article  = article,
                    onClick  = { onArticleClick(article) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ── Stats ──────────────────────────────────────────────────────────
        item(key = "stats") {
            StatsStrip(
                totalArticles = state.totalArticles,
                bookmarkCount = state.bookmarkCount,
                modifier      = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Greeting
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun GreetingSection() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val (greeting, icon) = when {
        hour in 5..11  -> "Good Morning" to Icons.Filled.WbSunny
        hour in 12..16 -> "Good Afternoon" to Icons.Filled.WbSunny
        hour in 17..20 -> "Good Evening" to Icons.Filled.NightsStay
        else           -> "Good Night" to Icons.Filled.DarkMode
    }

    val dateText = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        .format(calendar.time)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    greeting,
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint     = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Featured Article Hero Card
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun FeaturedArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model              = article.imageUrl,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "FEATURED",
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style      = MaterialTheme.typography.labelSmall,
                        color      = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = article.title,
                    style      = MaterialTheme.typography.titleLarge,
                    color      = Color.White,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint     = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = formatRelativeTime(article.publishedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Trending Article Card (horizontal scroll)
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun TrendingArticleCard(
    article: NewsArticle,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model              = article.imageUrl,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text  = article.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = article.category.accentColor()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = article.title,
                    style      = MaterialTheme.typography.bodySmall,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Category Card
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun CategoryCard(
    category: NewsCategory,
    articleCount: Int,
    onClick: () -> Unit
) {
    val accent = category.accentColor()

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = category.categoryIcon(),
                    contentDescription = null,
                    tint               = accent,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text       = category.displayName,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text  = "$articleCount articles",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// AI Assistant Card
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun AiAssistantCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "AI News Assistant",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    "Ask questions about today\u2019s headlines",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Latest Headline Card (compact horizontal)
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun LatestHeadlineCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            if (article.imageUrl.isNotBlank()) {
                AsyncImage(
                    model              = article.imageUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                )
            }
            Column(
                modifier            = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = article.title,
                    style      = MaterialTheme.typography.titleSmall,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = article.category.accentColor().copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            article.category.displayName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = article.category.accentColor()
                        )
                    }
                    Text(
                        formatRelativeTime(article.publishedAt),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Stats Strip
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun StatsStrip(
    totalArticles: Int,
    bookmarkCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatPill(Icons.Filled.Article,          "$totalArticles", "Cached",  Modifier.weight(1f))
        StatPill(Icons.Outlined.BookmarkBorder, "$bookmarkCount", "Saved",   Modifier.weight(1f))
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        color    = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Helpers
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier   = modifier
    )
}

private fun NewsCategory.categoryIcon(): ImageVector = when (this) {
    NewsCategory.WORLD         -> Icons.Filled.Public
    NewsCategory.ENTERTAINMENT -> Icons.Filled.Movie
    NewsCategory.BUSINESS      -> Icons.Filled.TrendingUp
    NewsCategory.TECHNOLOGY    -> Icons.Filled.Devices
    NewsCategory.POLITICS      -> Icons.Filled.AccountBalance
}

private fun formatRelativeTime(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        val date = parser.parse(dateStr) ?: return dateStr
        val diff = System.currentTimeMillis() - date.time
        val minutes = diff / (60 * 1000)
        val hours = diff / (3_600_000)
        val days = diff / (86_400_000)
        when {
            minutes < 1  -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24   -> "${hours}h ago"
            days < 7     -> "${days}d ago"
            else         -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    } catch (_: Exception) {
        dateStr
    }
}
