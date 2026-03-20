package com.news.skynet

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * HiltTestRunner.kt
 *
 * Custom [AndroidJUnitRunner] that replaces the application class with
 * [HiltTestApplication] during instrumented tests, enabling Hilt injection
 * in test environments without touching production code.
 *
 * Referenced in app/build.gradle:
 *   testInstrumentationRunner "com.news.skynet.HiltTestRunner"
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, name: String, context: Context): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
