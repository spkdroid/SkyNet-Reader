package com.dija.skynetreader.domain.usecase

import com.dija.skynetreader.domain.model.NewsArticle
import com.dija.skynetreader.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsByCategoryUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(type: Int): Flow<List<NewsArticle>> = repository.getTopHeadlines(type)
}