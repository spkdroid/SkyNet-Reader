package com.news.skynet.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * NewsApiService.kt
 *
 * Retrofit interface that declares every HTTP endpoint consumed by the app.
 * All functions are suspend — they are called from coroutines in the
 * repository or data source layer, never from the UI directly.
 *
 * Base URL: http://www.spkdroid.com/News/
 */
interface NewsApiService {

    /**
     * Fetches a list of news articles for the given category type.
     * @param type  Category identifier: 1=World, 2=Entertainment, 3=Business,
     *              4=Technology, 5=Politics
     */
    @GET("canada.php")
    suspend fun getNewsFeed(@Query("type") type: Int): List<NewsDto>

    /**
     * Full-text search across all categories.
     * @param query  Search keyword(s)
     */
    @GET("search.php")
    suspend fun searchArticles(@Query("q") query: String): List<NewsDto>
}
