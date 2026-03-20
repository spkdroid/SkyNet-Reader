package com.news.skynet.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.room.Room
import com.news.skynet.MainActivity
import com.news.skynet.R
import com.news.skynet.data.local.NewsDatabase
import com.news.skynet.data.local.NewsEntity
import kotlinx.coroutines.flow.first

/**
 * Glance App Widget that displays the top 5 latest headlines.
 * Tapping a headline deep-links into the app via the skynet:// scheme.
 */
class NewsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val articles = loadLatestArticles(context)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(Color.WHITE))
                        .padding(12.dp)
                        .cornerRadius(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            provider = ImageProvider(R.mipmap.ic_launcher),
                            contentDescription = null,
                            modifier = GlanceModifier.size(24.dp)
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            text = "SkyNet Reader",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(GlanceModifier.height(8.dp))

                    if (articles.isEmpty()) {
                        Box(
                            modifier = GlanceModifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No articles yet.\nOpen the app to refresh.",
                                style = TextStyle(fontSize = 13.sp)
                            )
                        }
                    } else {
                        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                            items(articles) { article ->
                                WidgetHeadlineItem(context, article)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetHeadlineItem(context: Context, article: NewsEntity) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = "skynet://article?url=${article.url}".toUri()
        }

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(actionStartActivity(intent))
        ) {
            Text(
                text = article.title,
                maxLines = 2,
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp)
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = article.publishedAt.take(25),
                maxLines = 1,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(Color.GRAY)
                )
            )
        }
    }

    private suspend fun loadLatestArticles(context: Context): List<NewsEntity> {
        val db = Room.databaseBuilder(
            context, NewsDatabase::class.java, NewsDatabase.DATABASE_NAME
        ).build()
        return try {
            db.newsDao().getArticlesByCategory(1).first().take(5)
        } catch (_: Exception) {
            emptyList()
        }
    }
}

/**
 * BroadcastReceiver that Android calls to manage the widget lifecycle.
 */
class NewsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NewsWidget()
}
