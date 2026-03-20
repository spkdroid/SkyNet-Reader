package com.news.skynet.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.news.skynet.data.repository.NewsRepository
import com.news.skynet.domain.model.NewsCategory
import com.news.skynet.util.NetworkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Background worker that pre-fetches all news categories into the Room
 * database so articles are available immediately on next launch (offline-first).
 *
 * Scheduled via WorkManager as a periodic task (e.g. every 6 hours) while on
 * an unmetered network.
 */
@HiltWorker
class NewsRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            var anyError = false
            for (category in NewsCategory.values()) {
                val result = newsRepository.getNewsFeed(category).first { it !is NetworkResult.Loading }
                if (result is NetworkResult.Error) {
                    anyError = true
                }
            }
            if (anyError) Result.retry() else Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "news_refresh_worker"
    }
}
