package com.news.skynet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * NewsDao.kt
 *
 * Data Access Object for the [NewsEntity] table.
 * All read operations return a [Flow] so Room notifies the repository
 * whenever the underlying data changes.
 */
@Dao
interface NewsDao {

    @Query("SELECT * FROM news_articles WHERE categoryType = :categoryType ORDER BY cachedAt DESC")
    fun getArticlesByCategory(categoryType: Int): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_articles WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsEntity>)

    @Query("DELETE FROM news_articles WHERE categoryType = :categoryType")
    suspend fun deleteByCategory(categoryType: Int)

    /** Remove stale cache entries older than [olderThanMillis]. */
    @Query("DELETE FROM news_articles WHERE cachedAt < :olderThanMillis")
    suspend fun deleteStaleArticles(olderThanMillis: Long)

    /** Total number of cached articles across all categories. */
    @Query("SELECT COUNT(*) FROM news_articles")
    fun getTotalArticleCount(): Flow<Int>
}
