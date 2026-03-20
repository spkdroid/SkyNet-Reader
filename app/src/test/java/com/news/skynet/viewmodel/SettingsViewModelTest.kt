package com.news.skynet.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.news.skynet.ui.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dsScope = kotlinx.coroutines.CoroutineScope(testDispatcher + Job())
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataStore = PreferenceDataStoreFactory.create(
            scope = dsScope,
            produceFile = { File(tmpFolder.root, "settings_test.preferences_pb") }
        )
        viewModel = SettingsViewModel(dataStore)
    }

    @After
    fun teardown() {
        dsScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `dark mode defaults to false`() = runTest(testDispatcher) {
        val value = viewModel.isDarkMode.first()
        assertFalse(value)
    }

    @Test
    fun `setDarkMode persists value`() = runTest(testDispatcher) {
        viewModel.setDarkMode(true)
        val value = viewModel.isDarkMode.first()
        assertTrue(value)
    }

    @Test
    fun `breaking news defaults to true`() = runTest(testDispatcher) {
        val value = viewModel.isBreakingNewsEnabled.first()
        assertTrue(value)
    }

    @Test
    fun `setBreakingNews disabled persists`() = runTest(testDispatcher) {
        viewModel.setBreakingNews(false)
        val value = viewModel.isBreakingNewsEnabled.first()
        assertFalse(value)
    }

    @Test
    fun `font size defaults to 16f`() = runTest(testDispatcher) {
        val value = viewModel.fontSize.first()
        assertEquals(16f, value)
    }

    @Test
    fun `setFontSize persists value`() = runTest(testDispatcher) {
        viewModel.setFontSize(20f)
        val value = viewModel.fontSize.first()
        assertEquals(20f, value)
    }
}
