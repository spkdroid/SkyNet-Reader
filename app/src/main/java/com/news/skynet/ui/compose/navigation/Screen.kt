package com.news.skynet.ui.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe route definitions for Compose Navigation.
 * Bottom-navigation items carry a [label] and [icon] for use in the NavBar.
 */
sealed class Screen(val route: String) {

    // ── Onboarding ─────────────────────────────────────────────────────────
    object Onboarding : Screen("onboarding")

    // ── Bottom-nav top-level destinations ─────────────────────────────────
    object Dashboard  : Screen("dashboard")
    object Feed       : Screen("feed")
    object Chat       : Screen("chat")
    object Search     : Screen("search")
    object Bookmarks  : Screen("bookmarks")

    // ── Secondary / detail screens ─────────────────────────────────────────
    object ArticleDetail : Screen("article_detail")
    object Settings      : Screen("settings")

    companion object {
        /** Ordered list that drives the BottomNavigationBar. */
        val bottomNavItems: List<BottomNavItem> = listOf(
            BottomNavItem("dashboard",  "Home",      Icons.Filled.Dashboard),
            BottomNavItem("feed",       "News",      Icons.Filled.Home),
            BottomNavItem("chat",       "AI Chat",   Icons.Filled.AutoAwesome),
            BottomNavItem("search",     "Search",    Icons.Filled.Search),
            BottomNavItem("bookmarks",  "Bookmarks", Icons.Filled.Bookmark),
        )

        /** Routes where the bottom nav should be visible. */
        val bottomNavRoutes: Set<String> = bottomNavItems.map { it.route }.toSet()
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
