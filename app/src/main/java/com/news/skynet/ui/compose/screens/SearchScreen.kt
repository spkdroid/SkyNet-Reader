package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.news.skynet.domain.model.NewsArticle
import com.news.skynet.ui.compose.components.NewsArticleCard
import com.news.skynet.ui.search.SearchUiState
import com.news.skynet.ui.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onArticleClick: (NewsArticle) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query   by rememberSaveable { mutableStateOf("") }
    var active  by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query             = query,
            onQueryChange     = { query = it; viewModel.onQueryChanged(it) },
            onSearch          = { active = false },
            active            = active,
            onActiveChange    = { active = it },
            placeholder       = { Text("Search articles…") },
            leadingIcon       = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (active) 0.dp else 16.dp)
        ) {
            // Search suggestions go here (future enhancement)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val s = uiState) {
                is SearchUiState.Idle  -> Text(
                    "Type at least 2 characters to search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                is SearchUiState.Loading -> CircularProgressIndicator()
                is SearchUiState.Empty   -> Text(
                    "No results for \"$query\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                is SearchUiState.Success -> {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.articles, key = { it.id }) { article ->
                            NewsArticleCard(
                                article = article,
                                onClick = { onArticleClick(article) }
                            )
                        }
                    }
                }
                is SearchUiState.Error -> Text(
                    s.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
