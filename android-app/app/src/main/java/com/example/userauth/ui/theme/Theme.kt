package com.example.userauth.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================
// Light Color Scheme - Brand Design
// ============================================
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = AccentCyan,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    outline = Outline
)

// ============================================
// Dark Color Scheme - Brand Design
// ============================================
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimary,
    primaryContainer = Primary.copy(alpha = 0.3f),
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary.copy(alpha = 0.3f),
    onSecondaryContainer = SecondaryLight,
    tertiary = AccentCyan,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = ErrorLight,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.3f),
    onErrorContainer = ErrorLight,
    outline = Color(0xFF444444)
)

@Composable
fun UserAuthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}