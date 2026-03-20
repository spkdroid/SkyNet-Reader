package com.news.skynet.ui.compose

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity-scoped ViewModel that handles app-level state:
 *  - Whether onboarding has been completed (navigation start destination)
 *  - Current theme (dark mode toggle)
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_DARK_MODE           = booleanPreferencesKey("dark_mode")
    }

    /**
     * `null` = still loading from disk; `true/false` = resolved.
     * MainActivity waits for non-null before calling setContent so there
     * is no flash of the wrong start destination.
     */
    val onboardingComplete = dataStore.data
        .map { it[KEY_ONBOARDING_COMPLETE] }         // null while loading
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)

    val isDarkMode = dataStore.data
        .map { it[KEY_DARK_MODE] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = true }
        }
    }
}
