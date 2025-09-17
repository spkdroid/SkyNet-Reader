package com.dija.skynetreader.domain.repository

import com.dija.skynetreader.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow


interface NewsRepository {
    suspend fun getTopHeadlines(newsType: Int): Flow<List<NewsArticle>>
}