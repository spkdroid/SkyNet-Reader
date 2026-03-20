package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.news.skynet.ui.chat.ChatMessage
import com.news.skynet.ui.chat.ChatUiState
import com.news.skynet.ui.chat.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val uiState  by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is ChatUiState.ModelNotFound -> ModelSetupPanel(onRetry = { viewModel.checkModelAndLoad() })
            is ChatUiState.ModelLoading  -> ModelLoadingPanel()
            else                         -> ChatPanel(
                messages = messages,
                isGenerating = uiState is ChatUiState.Generating,
                onSend   = { viewModel.sendMessage(it) },
                onClear  = { viewModel.clearConversation() }
            )
        }
    }
}

// ── Model not found ──────────────────────────────────────────────────────────

@Composable
private fun ModelSetupPanel(onRetry: () -> Unit) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint     = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text("AI Model Required", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Push the Gemma 3 1B model to the device via ADB:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text     = "adb push gemma-3-1b-it-int4.task /data/data/com.news.skynet/files/",
                modifier = Modifier.padding(12.dp),
                style    = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color    = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRetry) { Text("Try Again") }
    }
}

// ── Model loading ────────────────────────────────────────────────────────────

@Composable
private fun ModelLoadingPanel() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Loading AI model…", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ── Chat UI ──────────────────────────────────────────────────────────────────

@Composable
private fun ChatPanel(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    onSend: (String) -> Unit,
    onClear: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Header row
        Row(
            modifier       = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gemma 3 · 1B", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (messages.isNotEmpty()) {
                androidx.compose.material3.TextButton(onClick = onClear) { Text("Clear") }
            }
        }

        // Generating indicator
        if (isGenerating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Message list
        LazyColumn(
            state          = listState,
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg -> ChatBubble(msg) }
        }

        // Input bar
        Row(
            modifier       = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value          = input,
                onValueChange  = { input = it },
                placeholder    = { Text("Ask a question…") },
                modifier       = Modifier.weight(1f),
                shape          = RoundedCornerShape(24.dp),
                singleLine     = false,
                maxLines       = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (input.isNotBlank()) { onSend(input.trim()); input = "" }
                })
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick  = {
                    if (input.isNotBlank() && !isGenerating) { onSend(input.trim()); input = "" }
                },
                enabled  = input.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// ── Individual message bubble ─────────────────────────────────────────────────

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.role == ChatMessage.Role.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment   = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint     = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart    = 16.dp, topEnd      = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd   = if (isUser) 4.dp  else 16.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            val displayText = if (msg.isStreaming && msg.text.isEmpty()) "●" else {
                if (msg.isStreaming) msg.text + " ●" else msg.text
            }
            Text(
                text  = displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
