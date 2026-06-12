package com.jobtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SoftLavender,
    onPrimary = DeepNavy,
    primaryContainer = SlateBlue,
    onPrimaryContainer = TextPrimary,
    secondary = VibrantMint,
    onSecondary = DeepNavy,
    secondaryContainer = SlateBlue,
    onSecondaryContainer = TextPrimary,
    tertiary = CoralPink,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateBlue,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassUltraLight,
    error = iOSRed,
    onError = DeepNavy,
    inverseSurface = TextPrimary,
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
