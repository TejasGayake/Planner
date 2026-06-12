package com.jobtracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jobtracker.ui.theme.GlassUltraLight
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.TextTertiary
import com.jobtracker.ui.theme.iOSGreen
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.VibrantMint

data class StepperStep(
    val label: String,
    val key: String
)

val defaultSteps = listOf(
    StepperStep("Saved", "New"),
    StepperStep("Applied", "Applied"),
    StepperStep("Interview", "Interviewing"),
    StepperStep("Result", "Offer"),
    StepperStep("", "Rejected")
)

/**
 * A horizontal step indicator showing job application progress.
 * Each completed step fills with a gradient, the current step pulses.
 */
@Composable
fun StatusStepper(
    currentStatus: String,
    steps: List<StepperStep> = defaultSteps,
    onStepClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentStepIndex = steps.indexOfFirst { it.key == currentStatus }
    val displayIndex = if (currentStepIndex < 0) 0 else currentStepIndex

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < displayIndex
            val isCurrent = index == displayIndex
            val isRejected = step.key == "Rejected"
            val isOffer = step.key == "Offer"

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Circle indicator with connecting line
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Connecting line before this step (except first)
                    if (index > 0) {
                        val lineProgress by animateFloatAsState(
                            targetValue = if (isCompleted || isCurrent) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = 300f
                            ),
                            label = "stepperLine_$index"
                        )
                        Canvas(
                            modifier = Modifier
                                .size(20.dp, 4.dp)
                                .padding(horizontal = 2.dp)
                        ) {
                            val lineColor = if (isCompleted || isCurrent) {
                                VibrantMint
                            } else {
                                GlassUltraLight
                            }
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width * lineProgress, size.height / 2),
                                strokeWidth = 3f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Circle dot
                    val circleSize = 28.dp
                    val circleColor = when {
                        isRejected && (isCompleted || isCurrent) -> com.jobtracker.ui.theme.iOSRed
                        isOffer && (isCompleted || isCurrent) -> iOSGreen
                        isCompleted -> VibrantMint
                        isCurrent -> SoftLavender
                        else -> GlassUltraLight
                    }

                    val circleScale by animateFloatAsState(
                        targetValue = if (isCurrent) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = 300f
                        ),
                        label = "stepperCircle_$index"
                    )

                    Canvas(
                        modifier = Modifier.size(circleSize)
                    ) {
                        val radius = size.minDimension / 2f * circleScale
                        drawCircle(
                            color = circleColor,
                            radius = radius
                        )
                        if (isCompleted || isCurrent) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                radius = radius * 0.4f
                            )
                        }
                    }
                }

                // Label text
                if (step.label.isNotEmpty()) {
                    Text(
                        text = step.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted || isCurrent) {
                            GlassWhite
                        } else {
                            TextTertiary
                        },
                        fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
