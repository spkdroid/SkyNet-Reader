package com.news.skynet.data.remote

import com.google.gson.annotations.SerializedName

/**
 * NewsDto.kt
 *
 * Data Transfer Object — mirrors the JSON shape returned by the News API.
 * This class is only used in the data layer; it is mapped to the domain
 * model [NewsArticle] by [NewsRemoteDataSource].
 */
data class NewsDto(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val newsLine: String? = null,

    @SerializedName("link")
    val url: String? = null,

    @SerializedName("temp")
    val imageUrl: String? = null,

    @SerializedName("date")
    val date: String? = null
)
