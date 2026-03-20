package com.news.skynet.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.news.skynet.ui.compose.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dsScope = kotlinx.coroutines.CoroutineScope(testDispatcher + Job())
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataStore = PreferenceDataStoreFactory.create(
            scope = dsScope,
            produceFile = { File(tmpFolder.root, "app_test.preferences_pb") }
        )
        viewModel = AppViewModel(dataStore)
    }

    @After
    fun teardown() {
        dsScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `onboardingComplete resolves to false for fresh install`() = runTest(testDispatcher) {
        val value = viewModel.onboardingComplete.filterNotNull().first()
        assertFalse(value)
    }

    @Test
    fun `completeOnboarding sets value to true`() = runTest(testDispatcher) {
        viewModel.completeOnboarding()
        val value = viewModel.onboardingComplete.filterNotNull().first()
        assertTrue(value)
    }

    @Test
    fun `isDarkMode defaults to false`() = runTest(testDispatcher) {
        val value = viewModel.isDarkMode.first()
        assertFalse(value)
    }

    @Test
    fun `isDarkMode reflects DataStore changes`() = runTest(testDispatcher) {
        dataStore.edit { it[AppViewModel.KEY_DARK_MODE] = true }
        val value = viewModel.isDarkMode.first()
        assertTrue(value)
    }
}
