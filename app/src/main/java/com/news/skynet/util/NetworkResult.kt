package com.news.skynet.util

/**
 * NetworkResult.kt
 *
 * A generic sealed class that wraps the three possible states of any
 * asynchronous network/database operation in the app.
 *
 *  Loading  — request in-flight; UI should show shimmer/progress
 *  Success  — data available; UI should render content
 *  Error    — something went wrong; UI should show an error state with [message]
 */
sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()

    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null
    ) : NetworkResult<Nothing>()
}
