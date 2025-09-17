package com.dija.skynetreader.presentation.page

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.R
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dija.skynetreader.domain.model.NewsArticle
import com.dija.skynetreader.presentation.viewmodel.NewsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun NewsScreen(viewModel: NewsViewModel = hiltViewModel()) {
    val categories = listOf("World", "Business", "Technology", "Sports")
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Call ViewModel when tab changes
    LaunchedEffect(selectedTabIndex) {
        viewModel.loadNews((selectedTabIndex+1))
    }

    val articles by viewModel.articles.collectAsState()

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(category) }
                )
            }
        }
        LazyColumn {
            items(articles) { article ->
                NewsItemCard(article)
            }
        }
    }
}

@Composable
fun NewsItemCard(article: NewsArticle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // Image
            article.temp?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = article.title.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    onLoading = { Log.d("AsyncImage", "Loading: $url") },
                    onSuccess = { Log.d("AsyncImage", "Success: $url") },
                    onError = { Log.e("AsyncImage", "Error loading: $url", it.result.throwable) }
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = article.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = article.description.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
