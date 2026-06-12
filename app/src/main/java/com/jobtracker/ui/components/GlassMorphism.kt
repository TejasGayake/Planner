package com.jobtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobtracker.ui.theme.GlassBorder
import com.jobtracker.ui.theme.GlassWhiteLight

/**
 * Applies the Liquid Glass frosted-background effect to any composable.
 * Use as: modifier = Modifier.glassBackground(cornerRadius = 28.dp)
 *
 * Produces a visible glass gradient with a crisp border so surfaces
 * are clearly distinguishable from the background.
 */
fun Modifier.glassBackground(
    alpha: Float = 0.18f,
    cornerRadius: Dp = 28.dp
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        Brush.linearGradient(
            colors = listOf(
                GlassWhiteLight.copy(alpha = alpha),
                GlassWhiteLight.copy(alpha = alpha * 0.4f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
    .border(
        width = 1.dp,
        color = GlassBorder,
        shape = RoundedCornerShape(cornerRadius)
    )
