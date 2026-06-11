package com.jobtracker.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jobtracker.ui.theme.BlobBlue1
import com.jobtracker.ui.theme.BlobGreen1
import com.jobtracker.ui.theme.BlobTeal1
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated gradient blob background that slowly morphs organic shapes
 * to create a dynamic Liquid Glass backdrop.
 */
@Composable
fun BlobBackground(
    modifier: Modifier = Modifier,
    color1: Color = BlobBlue1,
    color2: Color = BlobGreen1,
    color3: Color = BlobTeal1
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blobTransition")

    // Slow sine-wave animation values for organic movement
    val progress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blobProgress1"
    )

    val progress2 by infiniteTransition.animateFloat(
        initialValue = PI.toFloat(),
        targetValue = 3f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blobProgress2"
    )

    val progress3 by infiniteTransition.animateFloat(
        initialValue = PI.toFloat() / 2f,
        targetValue = 2.5f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blobProgress3"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Blob 1 — large, slow blue-purple blob
        val r1 = w * 0.45f
        val bounce1 = sin(progress1) * 30f
        val blob1Path = createBlobPath(
            centerX = cx + cos(progress1 * 0.7f) * 40f,
            centerY = cy + sin(progress2 * 0.5f) * 30f,
            radius = r1 + bounce1,
            points = 8,
            variance = 0.3f,
            phase = progress1
        )

        drawPath(
            path = blob1Path,
            brush = Brush.radialGradient(
                colors = listOf(
                    color1.copy(alpha = 0.25f),
                    color1.copy(alpha = 0.05f)
                ),
                center = Offset(
                    cx + cos(progress1 * 0.7f) * 40f,
                    cy + sin(progress2 * 0.5f) * 30f
                ),
                radius = r1 + bounce1
            ),
            alpha = 0.5f
        )

        // Blob 2 — medium green-teal blob
        val r2 = w * 0.3f
        val blob2Path = createBlobPath(
            centerX = cx * 1.3f + cos(progress2) * 50f,
            centerY = cy * 0.6f + sin(progress3) * 40f,
            radius = r2 + sin(progress2) * 20f,
            points = 7,
            variance = 0.4f,
            phase = progress2
        )

        drawPath(
            path = blob2Path,
            brush = Brush.radialGradient(
                colors = listOf(
                    color2.copy(alpha = 0.2f),
                    color2.copy(alpha = 0.03f)
                ),
                center = Offset(
                    cx * 1.3f + cos(progress2) * 50f,
                    cy * 0.6f + sin(progress3) * 40f
                ),
                radius = r2 + 30f
            ),
            alpha = 0.4f
        )

        // Blob 3 — smaller teal accent blob
        val r3 = w * 0.2f
        val blob3Path = createBlobPath(
            centerX = cx * 0.5f + cos(progress3 * 0.8f) * 60f,
            centerY = cy * 1.3f + sin(progress1 * 0.6f) * 50f,
            radius = r3 + cos(progress3) * 15f,
            points = 6,
            variance = 0.35f,
            phase = progress3
        )

        drawPath(
            path = blob3Path,
            brush = Brush.radialGradient(
                colors = listOf(
                    color3.copy(alpha = 0.15f),
                    color3.copy(alpha = 0.02f)
                ),
                center = Offset(
                    cx * 0.5f + cos(progress3 * 0.8f) * 60f,
                    cy * 1.3f + sin(progress1 * 0.6f) * 50f
                ),
                radius = r3 + 20f
            ),
            alpha = 0.3f
        )
    }
}

/**
 * Creates an organic, irregular blob Path using trigonometric functions
 * to produce smooth, morphing shapes.
 */
private fun createBlobPath(
    centerX: Float,
    centerY: Float,
    radius: Float,
    points: Int = 8,
    variance: Float = 0.3f,
    phase: Float = 0f
): Path {
    val path = Path()
    val angleStep = (2f * PI.toFloat()) / points

    var startX = 0f
    var startY = 0f

    for (i in 0 until points) {
        val angle = i * angleStep + phase * 0.2f
        val variation = 1f + sin(angle * 3f + phase) * variance
        val r = radius * variation
        val x = centerX + cos(angle) * r
        val y = centerY + sin(angle) * r

        if (i == 0) {
            path.moveTo(x, y)
            startX = x
            startY = y
        } else {
            // Use cubic bezier for smooth curves between points
            val prevAngle = (i - 1) * angleStep + phase * 0.2f
            val prevVariation = 1f + sin(prevAngle * 3f + phase) * variance
            val prevR = radius * prevVariation
            val prevX = centerX + cos(prevAngle) * prevR
            val prevY = centerY + sin(prevAngle) * prevR

            val cp1x = prevX + cos(prevAngle + angleStep * 0.5f) * prevR * 0.5f
            val cp1y = prevY + sin(prevAngle + angleStep * 0.5f) * prevR * 0.5f
            val cp2x = x - cos(angle - angleStep * 0.5f) * r * 0.5f
            val cp2y = y - sin(angle - angleStep * 0.5f) * r * 0.5f

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, x, y)
        }
    }

    // Close the path smoothly back to start
    val lastAngle = (points - 1) * angleStep + phase * 0.2f
    val lastVariation = 1f + sin(lastAngle * 3f + phase) * variance
    val lastR = radius * lastVariation
    val lastX = centerX + cos(lastAngle) * lastR
    val lastY = centerY + sin(lastAngle) * lastR
    val cp1x = lastX + cos(lastAngle + angleStep * 0.5f) * lastR * 0.5f
    val cp1y = lastY + sin(lastAngle + angleStep * 0.5f) * lastR * 0.5f
    val cp2x = startX - cos(-angleStep * 0.5f + phase * 0.2f) * radius * 0.5f
    val cp2y = startY - sin(-angleStep * 0.5f + phase * 0.2f) * radius * 0.5f
    path.cubicTo(cp1x, cp1y, cp2x, cp2y, startX, startY)
    path.close()

    return path
}
