package com.news.skynet.ui.compose.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.ui.compose.screens.ArticleDetailScreen
import com.news.skynet.ui.compose.screens.BookmarksScreen
import com.news.skynet.ui.compose.screens.ChatScreen
import com.news.skynet.ui.compose.screens.DashboardScreen
import com.news.skynet.ui.compose.screens.NewsFeedScreen
import com.news.skynet.ui.compose.screens.OnboardingScreen
import com.news.skynet.ui.compose.screens.SearchScreen
import com.news.skynet.ui.compose.screens.SettingsScreen

/**
 * Root Composable.
 *
 * For first-run launches [startOnboarding] = true and the app starts at the
 * Onboarding route; after the user completes it the graph navigates to Dashboard.
 * Returning users go straight to Dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkyNetNavGraph(
    startOnboarding: Boolean,
    onOnboardingComplete: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    // Shared: carries the article tapped by the user across the nav back stack
    val selectedArticleVm: SelectedArticleViewModel = hiltViewModel()

    val startDestination = if (startOnboarding) Screen.Onboarding.route
                           else Screen.Dashboard.route

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomNavRoutes

    @OptIn(ExperimentalMaterial3Api::class)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier  = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar    = {
            if (currentRoute != Screen.Onboarding.route) {
                SkyNetTopBar(
                    currentRoute   = currentRoute,
                    scrollBehavior = scrollBehavior,
                    onSearchClick  = { navController.navigate(Screen.Search.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    canNavigateBack = !showBottomBar && navController.previousBackStackEntry != null,
                    navigateBack   = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                SkyNetBottomBar(
                    currentRoute  = currentRoute,
                    onItemClick   = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        onOnboardingComplete()          // persist to DataStore
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onArticleClick = { article ->
                        selectedArticleVm.select(article)
                        navController.navigate(Screen.ArticleDetail.route)
                    },
                    onNavigateToFeed      = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onNavigateToChat      = { navController.navigate(Screen.Chat.route) },
                    onNavigateToSearch    = { navController.navigate(Screen.Search.route) },
                    onNavigateToBookmarks = { navController.navigate(Screen.Bookmarks.route) }
                )
            }

            composable(Screen.Feed.route) {
                NewsFeedScreen(
                    onArticleClick = { article ->
                        selectedArticleVm.select(article)
                        navController.navigate(Screen.ArticleDetail.route)
                    }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen()
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { article ->
                        selectedArticleVm.select(article)
                        navController.navigate(Screen.ArticleDetail.route)
                    }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onArticleClick = { article ->
                        selectedArticleVm.select(article)
                        navController.navigate(Screen.ArticleDetail.route)
                    }
                )
            }

            composable(Screen.ArticleDetail.route) {
                val article = selectedArticleVm.article
                if (article != null) {
                    ArticleDetailScreen(
                        article        = article,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.Settings.route) {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

// ── Top app bar ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkyNetTopBar(
    currentRoute: String?,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val title = when (currentRoute) {
        Screen.Dashboard.route     -> "SkyNet Reader"
        Screen.Feed.route          -> "News Feed"
        Screen.Chat.route          -> "AI Assistant"
        Screen.Search.route        -> "Search"
        Screen.Bookmarks.route     -> "Bookmarks"
        Screen.ArticleDetail.route -> "Article"
        Screen.Settings.route      -> "Settings"
        else                       -> "SkyNet Reader"
    }

    TopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (currentRoute in Screen.bottomNavRoutes) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text    = { Text("Settings") },
                    onClick = { showMenu = false; onSettingsClick() },
                    leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) }
                )
            }
        }
    )
}

// ── Bottom navigation bar ────────────────────────────────────────────────────

@Composable
private fun SkyNetBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar {
        Screen.bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon     = { Icon(item.icon, contentDescription = item.label) },
                label    = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick  = { onItemClick(item.route) }
            )
        }
    }
}
