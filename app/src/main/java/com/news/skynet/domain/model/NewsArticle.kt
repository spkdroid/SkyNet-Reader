package com.news.skynet.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * NewsArticle.kt
 *
 * The canonical domain model for a news article.
 * This is a pure Kotlin data class — no Android framework dependencies —
 * making it fully unit-testable. It is shared across all layers via the
 * domain module and mapped to/from NewsEntity (Room) and NewsDto (Retrofit).
 */
@Parcelize
data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: String,
    val category: NewsCategory,
    val isBookmarked: Boolean = false
) : Parcelable
