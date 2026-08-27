package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LightPolishPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPolishPrimaryLight,
    onPrimaryContainer = LightPolishPrimaryDark,
    secondary = LightPolishPrimaryAccent,
    onSecondary = Color.White,
    secondaryContainer = LightPolishPrimaryLight,
    onSecondaryContainer = LightPolishPrimary,
    background = LightPolishBg,
    onBackground = LightPolishTextPrimary,
    surface = LightPolishCardBg,
    onSurface = LightPolishTextPrimary,
    surfaceVariant = LightPolishInputBg,
    onSurfaceVariant = LightPolishTextSecondary,
    surfaceContainer = LightPolishCardBg,
    surfaceContainerHigh = LightPolishInputBg,
    surfaceContainerHighest = Color(0xFFE2E8F0),
    outline = LightPolishCardBorder,
    outlineVariant = LightPolishCardBorderSubtle,
    error = LightPolishStatusError,
    onError = Color.White,
    errorContainer = LightPolishStatusErrorBg,
    onErrorContainer = LightPolishStatusError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPolishPrimary,
    onPrimary = Color(0xFF0B0F17),
    primaryContainer = DarkPolishPrimaryLight,
    onPrimaryContainer = DarkPolishPrimary,
    secondary = DarkPolishPrimaryAccent,
    onSecondary = Color(0xFF0B0F17),
    secondaryContainer = DarkPolishPrimaryLight,
    onSecondaryContainer = DarkPolishPrimaryAccent,
    background = DarkPolishBg,
    onBackground = DarkPolishTextPrimary,
    surface = DarkPolishCardBg,
    onSurface = DarkPolishTextPrimary,
    surfaceVariant = DarkPolishInputBg,
    onSurfaceVariant = DarkPolishTextSecondary,
    surfaceContainer = DarkPolishCardBg,
    surfaceContainerHigh = DarkPolishInputBg,
    surfaceContainerHighest = DarkPolishCardBorder,
    outline = DarkPolishCardBorder,
    outlineVariant = DarkPolishCardBorderSubtle,
    error = DarkPolishStatusError,
    onError = Color.White,
    errorContainer = DarkPolishStatusErrorBg,
    onErrorContainer = DarkPolishStatusError
)

@Composable
fun VoltCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val voltCalcColors = if (darkTheme) DarkVoltCalcColors else LightVoltCalcColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalVoltCalcColors provides voltCalcColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
