package com.dija.skynetreader.data.remote

import com.dija.skynetreader.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("News/canada.php")
    suspend fun getNewsByCategory(
        @Query("type") type: Int
    ): Flow<List<NewsArticle>>
}