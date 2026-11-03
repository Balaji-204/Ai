package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF00252C),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFFA6F5FF),
    secondary = NeuralViolet,
    onSecondary = Color(0xFF280056),
    secondaryContainer = Color(0xFF531E8C),
    onSecondaryContainer = Color(0xFFEDDCFF),
    tertiary = OfflineEmerald,
    onTertiary = Color(0xFF003822),
    background = StudioBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = StudioSurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = StudioSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = StudioCardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = StudioPrimaryLight,
    onPrimary = Color.White,
    secondary = NeuralViolet,
    onSecondary = Color.White,
    tertiary = OfflineEmerald,
    background = StudioBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = StudioSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = StudioSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun OfflineAiStudioTheme(
    darkTheme: Boolean = true, // Default to sleek dark studio aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
