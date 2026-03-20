package com.news.skynet.ui.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.news.skynet.ui.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val isDarkMode      by viewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val breakingNews    by viewModel.isBreakingNewsEnabled.collectAsStateWithLifecycle(true)
    val fontSize        by viewModel.fontSize.collectAsStateWithLifecycle(SettingsViewModel.DEFAULT_FONT_SIZE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Appearance ───────────────────────────────────────────────────
        SectionHeader("Appearance")

        SettingsSwitchRow(
            title       = "Dark Mode",
            description = "Use dark colour scheme",
            checked     = isDarkMode,
            onCheckedChange = { viewModel.setDarkMode(it) }
        )

        HorizontalDivider()

        // ── Reading ──────────────────────────────────────────────────────
        SectionHeader("Reading")

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                "Article font size  (${fontSize.toInt()}sp)",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Slider(
                value         = fontSize,
                onValueChange = { viewModel.setFontSize(it) },
                valueRange    = 12f..24f,
                steps         = 5
            )
        }

        HorizontalDivider()

        // ── Notifications ────────────────────────────────────────────────
        SectionHeader("Notifications")

        SettingsSwitchRow(
            title       = "Breaking News Alerts",
            description = "Notify when major stories break",
            checked     = breakingNews,
            onCheckedChange = { viewModel.setBreakingNews(it) }
        )

        HorizontalDivider()

        // ── About ────────────────────────────────────────────────────────
        SectionHeader("About")

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text("SkyNet Reader", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text("Version 2.0", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                "Powered by MVVM + Clean Architecture, Jetpack Compose, Hilt DI, " +
                "Room database, and on-device Gemma AI via MediaPipe.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style     = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color     = MaterialTheme.colorScheme.primary,
        modifier  = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
