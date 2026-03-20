package com.news.skynet.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel
 *
 * Persists user preferences (dark mode, font size, notifications) to
 * DataStore<Preferences>. Exposes each preference as a [Flow] for
 * reactive consumption in [SettingsFragment].
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val KEY_DARK_MODE         = booleanPreferencesKey("dark_mode")
        val KEY_BREAKING_NEWS     = booleanPreferencesKey("breaking_news_notifications")
        val KEY_FONT_SIZE         = floatPreferencesKey("article_font_size")

        const val DEFAULT_FONT_SIZE = 16f
    }

    // ── Exposed flows ─────────────────────────────────────────────────────────

    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    val isBreakingNewsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_BREAKING_NEWS] ?: true
    }

    val fontSize: Flow<Float> = dataStore.data.map { prefs ->
        prefs[KEY_FONT_SIZE] ?: DEFAULT_FONT_SIZE
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    fun setBreakingNews(enabled: Boolean) = viewModelScope.launch {
        dataStore.edit { it[KEY_BREAKING_NEWS] = enabled }
    }

    fun setFontSize(size: Float) = viewModelScope.launch {
        dataStore.edit { it[KEY_FONT_SIZE] = size }
    }
}
