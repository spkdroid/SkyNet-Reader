package com.news.skynet.model

import com.news.skynet.domain.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class NewsCategoryTest {

    @Test
    fun `fromApiType returns correct category for each type`() {
        assertEquals(NewsCategory.WORLD, NewsCategory.fromApiType(1))
        assertEquals(NewsCategory.ENTERTAINMENT, NewsCategory.fromApiType(2))
        assertEquals(NewsCategory.BUSINESS, NewsCategory.fromApiType(3))
        assertEquals(NewsCategory.TECHNOLOGY, NewsCategory.fromApiType(4))
        assertEquals(NewsCategory.POLITICS, NewsCategory.fromApiType(5))
    }

    @Test
    fun `fromApiType defaults to WORLD for unknown type`() {
        assertEquals(NewsCategory.WORLD, NewsCategory.fromApiType(0))
        assertEquals(NewsCategory.WORLD, NewsCategory.fromApiType(99))
        assertEquals(NewsCategory.WORLD, NewsCategory.fromApiType(-1))
    }

    @Test
    fun `each category has correct display name`() {
        assertEquals("World News", NewsCategory.WORLD.displayName)
        assertEquals("Entertainment", NewsCategory.ENTERTAINMENT.displayName)
        assertEquals("Business", NewsCategory.BUSINESS.displayName)
        assertEquals("Technology", NewsCategory.TECHNOLOGY.displayName)
        assertEquals("Politics", NewsCategory.POLITICS.displayName)
    }

    @Test
    fun `each category has unique apiType`() {
        val apiTypes = NewsCategory.values().map { it.apiType }
        assertEquals(apiTypes.size, apiTypes.toSet().size)
    }

    @Test
    fun `values returns all five categories`() {
        assertEquals(5, NewsCategory.values().size)
    }
}
