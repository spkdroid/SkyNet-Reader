package com.news.skynet.di

import android.content.Context
import androidx.room.Room
import com.news.skynet.data.local.BookmarkDao
import com.news.skynet.data.local.NewsDao
import com.news.skynet.data.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule.kt
 *
 * Hilt module that constructs the Room [NewsDatabase] and exposes its
 * individual DAOs as injectable dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            NewsDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNewsDao(db: NewsDatabase): NewsDao = db.newsDao()

    @Provides
    fun provideBookmarkDao(db: NewsDatabase): BookmarkDao = db.bookmarkDao()
}
