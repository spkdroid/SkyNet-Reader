package com.news.skynet.domain.model

/**
 * NewsCategory.kt
 *
 * Represents the available news feed categories. Each category maps to a
 * tab in the main screen and to a query-parameter value in the News API.
 */
enum class NewsCategory(
    val displayName: String,
    val apiType: Int,
    val colorResId: String
) {
    WORLD("World News",       apiType = 1, colorResId = "cat_world"),
    ENTERTAINMENT("Entertainment", apiType = 2, colorResId = "cat_entertainment"),
    BUSINESS("Business",     apiType = 3, colorResId = "cat_business"),
    TECHNOLOGY("Technology", apiType = 4, colorResId = "cat_technology"),
    POLITICS("Politics",     apiType = 5, colorResId = "cat_politics");

    companion object {
        fun fromApiType(type: Int): NewsCategory =
            values().firstOrNull { it.apiType == type } ?: WORLD
    }
}
