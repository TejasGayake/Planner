package com.jobtracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jobtracker.ui.theme.GlassBorder
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.SoftLavender

/**
 * A selectable capsule-shaped filter pill for the dashboard filter bar.
 */
@Composable
fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 300f
        ),
        label = "pillPressScale"
    )

    val containerColor = if (isSelected) {
        SoftLavender.copy(alpha = 0.3f)
    } else {
        GlassWhiteLight.copy(alpha = 0.06f)
    }

    val borderColor = if (isSelected) {
        SoftLavender.copy(alpha = 0.6f)
    } else {
        GlassBorder
    }

    val textColor = if (isSelected) {
        SoftLavender
    } else {
        GlassWhite.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .scale(pressScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = if (count != null) "$text ($count)" else text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
    }
}
