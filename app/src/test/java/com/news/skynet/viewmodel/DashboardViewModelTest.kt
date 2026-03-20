package com.news.skynet.viewmodel

import app.cash.turbine.test
import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.NewsDao
import com.news.skynet.data.local.NewsEntity
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.ui.dashboard.DashboardUiState
import com.news.skynet.ui.dashboard.DashboardViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val newsDao: NewsDao = mockk()
    private val bookmarkDao: BookmarkDao = mockk()

    private fun makeEntity(id: String, cat: Int, imageUrl: String = "https://img.com/$id") =
        NewsEntity(
            id = id, title = "Title $id", summary = "Summary",
            url = "https://example.com/$id", imageUrl = imageUrl,
            publishedAt = "2026-01-01", categoryType = cat,
            cachedAt = System.currentTimeMillis()
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun setupDaoDefaults(
        worldEntities: List<NewsEntity> = emptyList(),
        entertainmentEntities: List<NewsEntity> = emptyList(),
        businessEntities: List<NewsEntity> = emptyList(),
        technologyEntities: List<NewsEntity> = emptyList(),
        politicsEntities: List<NewsEntity> = emptyList(),
        totalCount: Int = 0,
        bookmarkCount: Int = 0
    ) {
        every { newsDao.getArticlesByCategory(1) } returns flowOf(worldEntities)
        every { newsDao.getArticlesByCategory(2) } returns flowOf(entertainmentEntities)
        every { newsDao.getArticlesByCategory(3) } returns flowOf(businessEntities)
        every { newsDao.getArticlesByCategory(4) } returns flowOf(technologyEntities)
        every { newsDao.getArticlesByCategory(5) } returns flowOf(politicsEntities)
        every { newsDao.getTotalArticleCount() } returns flowOf(totalCount)
        every { bookmarkDao.getBookmarkCount() } returns flowOf(bookmarkCount)
    }

    @Test
    fun `initial state is empty DashboardUiState`() = runTest {
        setupDaoDefaults()
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        assertEquals(DashboardUiState(), viewModel.uiState.value)
    }

    @Test
    fun `uiState reflects total article and bookmark counts`() = runTest {
        val entities = listOf(makeEntity("1", 1), makeEntity("2", 1))
        setupDaoDefaults(worldEntities = entities, totalCount = 10, bookmarkCount = 3)
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem() // initial empty
            val state = awaitItem()
            assertEquals(10, state.totalArticles)
            assertEquals(3, state.bookmarkCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState provides category stats`() = runTest {
        setupDaoDefaults(
            worldEntities = listOf(makeEntity("1", 1), makeEntity("2", 1)),
            technologyEntities = listOf(makeEntity("3", 4)),
            totalCount = 3
        )
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem() // initial
            val state = awaitItem()
            assertEquals(5, state.categoryStats.size)

            val worldStat = state.categoryStats.first { it.category == NewsCategory.WORLD }
            assertEquals(2, worldStat.articleCount)

            val techStat = state.categoryStats.first { it.category == NewsCategory.TECHNOLOGY }
            assertEquals(1, techStat.articleCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `featured article is first with non-blank image`() = runTest {
        val entities = listOf(
            makeEntity("noimg", 1, imageUrl = ""),
            makeEntity("withimg", 1, imageUrl = "https://img.com/1")
        )
        setupDaoDefaults(worldEntities = entities, totalCount = 2)
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertNotNull(state.featuredArticle)
            assertEquals("withimg", state.featuredArticle!!.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `featured article is null when all images blank`() = runTest {
        val entities = listOf(makeEntity("1", 1, imageUrl = ""))
        setupDaoDefaults(worldEntities = entities, totalCount = 1)
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertNull(state.featuredArticle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trending articles exclude featured and limited to 8`() = runTest {
        val entities = (1..12).map { makeEntity("$it", 1) }
        setupDaoDefaults(worldEntities = entities, totalCount = 12)
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertTrue(state.trendingArticles.size <= 8)
            assertTrue(state.trendingArticles.none { it.id == state.featuredArticle?.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `latest articles limited to 15`() = runTest {
        val entities = (1..20).map { makeEntity("$it", 1) }
        setupDaoDefaults(worldEntities = entities, totalCount = 20)
        val viewModel = DashboardViewModel(newsDao, bookmarkDao)

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertTrue(state.latestArticles.size <= 15)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
