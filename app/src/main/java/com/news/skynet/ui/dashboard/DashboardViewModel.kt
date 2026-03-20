package com.news.skynet.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.NewsDao
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.domain.model.NewsCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryStat(
    val category: NewsCategory,
    val articleCount: Int
)

data class DashboardUiState(
    val totalArticles: Int = 0,
    val bookmarkCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val featuredArticle: NewsArticle? = null,
    val trendingArticles: List<NewsArticle> = emptyList(),
    val latestArticles: List<NewsArticle> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    newsDao: NewsDao,
    bookmarkDao: BookmarkDao
) : ViewModel() {

    private val categoryFlows = NewsCategory.values().map { cat ->
        newsDao.getArticlesByCategory(cat.apiType)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        newsDao.getTotalArticleCount(),
        bookmarkDao.getBookmarkCount(),
        combine(categoryFlows) { it.toList() }
    ) { total, bookmarks, categoryLists ->
        val categories = NewsCategory.values()

        val allArticles = categoryLists.flatMapIndexed { index, entities ->
            entities.map { entity -> entity to categories[index] }
        }
            .sortedByDescending { (entity, _) -> entity.cachedAt }
            .map { (entity, cat) ->
                NewsArticle(
                    id          = entity.id,
                    title       = entity.title,
                    summary     = entity.summary,
                    url         = entity.url,
                    imageUrl    = entity.imageUrl,
                    publishedAt = entity.publishedAt,
                    category    = cat
                )
            }

        val stats = categoryLists.mapIndexed { index, entities ->
            CategoryStat(categories[index], entities.size)
        }

        DashboardUiState(
            totalArticles    = total,
            bookmarkCount    = bookmarks,
            categoryStats    = stats,
            featuredArticle  = allArticles.firstOrNull { it.imageUrl.isNotBlank() },
            trendingArticles = allArticles.filter { it.imageUrl.isNotBlank() }.drop(1).take(8),
            latestArticles   = allArticles.take(15)
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )
}
