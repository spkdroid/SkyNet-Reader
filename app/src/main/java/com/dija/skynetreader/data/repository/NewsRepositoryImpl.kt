package com.dija.skynetreader.data.repository

import com.dija.skynetreader.data.remote.NewsApiService
import com.dija.skynetreader.domain.model.NewsArticle
import com.dija.skynetreader.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService
) : NewsRepository {
    override suspend fun getTopHeadlines(newsType: Int): Flow<List<NewsArticle>> {
        return api.getNewsByCategory(newsType)
    }
}