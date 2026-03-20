package com.news.skynet.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * BookmarkEntity.kt
 *
 * Room entity for bookmarked articles. Stored separately from the
 * news cache so bookmarks survive cache eviction.
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: String,
    val categoryType: Int,
    val savedAt: Long = System.currentTimeMillis()
)
