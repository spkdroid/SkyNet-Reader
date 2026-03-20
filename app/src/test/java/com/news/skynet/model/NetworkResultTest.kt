package com.news.skynet.model

import com.news.skynet.util.NetworkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultTest {

    @Test
    fun `Loading is singleton`() {
        assertTrue(NetworkResult.Loading === NetworkResult.Loading)
    }

    @Test
    fun `Success wraps data correctly`() {
        val result = NetworkResult.Success(listOf("a", "b"))
        assertEquals(listOf("a", "b"), result.data)
    }

    @Test
    fun `Success with empty data`() {
        val result = NetworkResult.Success(emptyList<String>())
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `Error contains message`() {
        val result = NetworkResult.Error("Something went wrong")
        assertEquals("Something went wrong", result.message)
    }

    @Test
    fun `Error code defaults to null`() {
        val result = NetworkResult.Error("error")
        assertNull(result.code)
    }

    @Test
    fun `Error with code`() {
        val result = NetworkResult.Error("Not found", code = 404)
        assertEquals(404, result.code)
    }

    @Test
    fun `type checks work correctly`() {
        val loading: NetworkResult<String> = NetworkResult.Loading
        val success: NetworkResult<String> = NetworkResult.Success("data")
        val error: NetworkResult<String> = NetworkResult.Error("err")

        assertTrue(loading is NetworkResult.Loading)
        assertTrue(success is NetworkResult.Success)
        assertTrue(error is NetworkResult.Error)
    }
}
