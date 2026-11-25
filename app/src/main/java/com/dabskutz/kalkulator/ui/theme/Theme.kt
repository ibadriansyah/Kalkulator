package com.dabskutz.kalkulator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalDarkTheme = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = LiquidGlassColor.DarkAccent,
    onPrimary = LiquidGlassColor.DarkTextPrimary,
    primaryContainer = LiquidGlassColor.DarkSurface,
    onPrimaryContainer = LiquidGlassColor.DarkTextPrimary,
    secondary = LiquidGlassColor.DarkOrange,
    onSecondary = LiquidGlassColor.DarkTextPrimary,
    secondaryContainer = LiquidGlassColor.DarkSurfaceLight,
    onSecondaryContainer = LiquidGlassColor.DarkTextPrimary,
    background = LiquidGlassColor.DarkBackground,
    onBackground = LiquidGlassColor.DarkTextPrimary,
    surface = LiquidGlassColor.DarkSurface,
    onSurface = LiquidGlassColor.DarkTextPrimary,
    surfaceVariant = LiquidGlassColor.DarkSurfaceLight,
    onSurfaceVariant = LiquidGlassColor.DarkTextSecondary,
    error = LiquidGlassColor.ActionClear,
    onError = LiquidGlassColor.DarkTextPrimary,
    outline = LiquidGlassColor.DarkDivider,
    scrim = Color(0x60000000L)
)

private val LightColorScheme = lightColorScheme(
    primary = LiquidGlassColor.LightAccent,
    onPrimary = LiquidGlassColor.LightTextPrimary,
    primaryContainer = LiquidGlassColor.LightSurfaceLight,
    onPrimaryContainer = LiquidGlassColor.LightTextPrimary,
    secondary = LiquidGlassColor.LightOrange,
    onSecondary = LiquidGlassColor.LightTextPrimary,
    secondaryContainer = LiquidGlassColor.LightSurfaceLight,
    onSecondaryContainer = LiquidGlassColor.LightTextPrimary,
    background = LiquidGlassColor.LightBackground,
    onBackground = LiquidGlassColor.LightTextPrimary,
    surface = LiquidGlassColor.LightSurface,
    onSurface = LiquidGlassColor.LightTextPrimary,
    surfaceVariant = LiquidGlassColor.LightSurfaceLight,
    onSurfaceVariant = LiquidGlassColor.LightTextSecondary,
    error = LiquidGlassColor.ActionClear,
    onError = LiquidGlassColor.LightTextPrimary,
    outline = LiquidGlassColor.LightDivider,
    scrim = Color(0x60000000L)
)

@Composable
fun LiquidGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LiquidGlassTypography.base,
            content = content
        )
    }
}
