package com.dija.skynetreader.data.repository

import com.dija.skynetreader.data.remote.NewsApiService
import com.dija.skynetreader.domain.model.NewsArticle
import com.dija.skynetreader.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService
) : NewsRepository {
    override fun getTopHeadlines(newsType: Int): Flow<List<NewsArticle>> {
        return flow {
            emit(api.getNewsByCategory(newsType))
        }
    }
}