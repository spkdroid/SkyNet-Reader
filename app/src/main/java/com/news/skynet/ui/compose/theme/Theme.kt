package com.news.skynet.ui.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary            = Blue40,
    onPrimary          = androidx.compose.ui.graphics.Color.White,
    primaryContainer   = BlueContainer,
    onPrimaryContainer = BlueContainer80,
    secondary          = Teal40,
    error              = Red40,
    surface            = Surface,
    onSurface          = OnSurface,
    surfaceVariant     = SurfaceVar,
    onSurfaceVariant   = OnSurfaceVar,
)

private val DarkColors = darkColorScheme(
    primary            = Blue80,
    onPrimary          = BlueContainer80,
    primaryContainer   = Blue40,
    onPrimaryContainer = BlueContainer,
    secondary          = Teal80,
    error              = Red80,
    surface            = SurfaceDark,
    onSurface          = OnSurfaceDark,
)

/**
 * SkyNetTheme wraps the entire application in Material3.
 *
 * • On Android 12+ the system dynamic colours are used when available.
 * • The [darkTheme] flag is driven by the user-chosen setting from DataStore,
 *   falling back to the system default when not set.
 */
@Composable
fun SkyNetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SkyNetTypography,
        content     = content
    )
}
