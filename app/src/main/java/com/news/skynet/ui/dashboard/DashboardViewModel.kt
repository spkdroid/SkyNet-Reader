package com.news.skynet.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.NewsDao
import com.news.skynet.data.local.NewsEntity
import com.news.skynet.domain.model.NewsCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Data backing a single category stat card on the Dashboard.
 */
data class CategoryStat(
    val category: NewsCategory,
    val articleCount: Int,
    val colorName: String       // e.g. "cat_world" — resolved at view layer via getIdentifier
)

/**
 * Full state object for the Dashboard screen.
 */
data class DashboardUiState(
    val totalArticles: Int = 0,
    val bookmarkCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val recentTitles: List<String> = emptyList()
)

/**
 * DashboardViewModel
 *
 * Aggregates counts from Room (cached articles + bookmarks) and exposes them
 * as a single [DashboardUiState] StateFlow.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val bookmarkDao: BookmarkDao
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        newsDao.getTotalArticleCount(),
        bookmarkDao.getBookmarkCount(),
        categoryFlow(NewsCategory.WORLD),
        categoryFlow(NewsCategory.TECHNOLOGY),
        categoryFlow(NewsCategory.BUSINESS)
    ) { total, bookmarks, world, tech, biz ->
        val recentTitles = (world + tech + biz)
            .sortedByDescending { it.cachedAt }
            .take(5)
            .map { it.title }

        val stats = NewsCategory.values().map { cat ->
            val count = when (cat) {
                NewsCategory.WORLD         -> world.size
                NewsCategory.TECHNOLOGY    -> tech.size
                NewsCategory.BUSINESS      -> biz.size
                else                       -> 0
            }
            CategoryStat(cat, count, cat.colorResId)
        }

        DashboardUiState(
            totalArticles  = total,
            bookmarkCount  = bookmarks,
            categoryStats  = stats,
            recentTitles   = recentTitles
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    // Count flow per category
    private fun categoryFlow(cat: NewsCategory): Flow<List<NewsEntity>> =
        newsDao.getArticlesByCategory(cat.apiType)
}
