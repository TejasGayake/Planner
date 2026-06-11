package com.jobtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SoftLavender,
    onPrimary = DeepNavy,
    primaryContainer = SlateBlue,
    onPrimaryContainer = GlassWhite,
    secondary = VibrantMint,
    onSecondary = DeepNavy,
    secondaryContainer = SlateBlue,
    onSecondaryContainer = GlassWhite,
    tertiary = CoralPink,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = GlassWhite,
    surface = DarkSurface,
    onSurface = GlassWhite,
    surfaceVariant = SlateBlue,
    onSurfaceVariant = CoolGray,
    outline = GlassBorder,
    outlineVariant = GlassUltraLight,
    error = iOSRed,
    onError = DeepNavy,
    inverseSurface = GlassWhite,
    inverseOnSurface = DeepNavy
)

@Composable
fun JobTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = JobTrackerTypography,
        content = content
    )
}
