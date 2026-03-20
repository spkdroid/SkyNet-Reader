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
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("newsline")
    val newsLine: String? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("image")
    val imageUrl: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("type")
    val type: Int? = null
)
