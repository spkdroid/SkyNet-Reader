package com.news.skynet.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * NewsEntity.kt
 *
 * Room persistence entity for cached news articles. The [category] field
 * is stored as its [Int] apiType so Room does not need a TypeConverter.
 * Cached rows are considered stale if [cachedAt] is older than 24 hours.
 */
@Entity(tableName = "news_articles")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: String,
    val categoryType: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
