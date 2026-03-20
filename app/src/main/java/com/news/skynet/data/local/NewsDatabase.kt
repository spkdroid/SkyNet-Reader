package com.news.skynet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * NewsDatabase.kt
 *
 * The single Room database instance for the entire application.
 * Hilt's [DatabaseModule] is responsible for constructing and providing
 * this singleton — no manual getInstance() calls needed.
 *
 * Version history:
 *  1 — initial schema (news_articles + bookmarks tables)
 */
@Database(
    entities = [NewsEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val DATABASE_NAME = "skynet_news.db"
    }
}
