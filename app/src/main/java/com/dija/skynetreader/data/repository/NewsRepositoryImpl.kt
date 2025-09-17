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
    override fun getTopHeadlines(apiKey: String): Flow<List<NewsArticle>> = flow {
        val newsArticleList =  api.getNewsByCategory(apiKey.toInt())
        emit(newsArticleList)
    }
}