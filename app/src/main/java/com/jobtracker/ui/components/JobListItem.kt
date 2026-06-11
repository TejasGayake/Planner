package com.jobtracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobtracker.data.db.Job
import com.jobtracker.ui.theme.CoralPink
import com.jobtracker.ui.theme.CoolGray
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.iOSGreen
import com.jobtracker.ui.theme.iOSOrange
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.VibrantMint
import com.jobtracker.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A glass card representing a single job listing in the dashboard list.
 */
@Composable
fun JobListItem(
    job: Job,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateDpAsState(
        targetValue = if (isPressed) 0.0.dp else 0.0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 300f
        ),
        label = "jobItemScale"
    )

    val scaleValue = if (isPressed) 0.98f else 1f

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleValue)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        cornerRadius = cornerRadius,
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Company name
                Text(
                    text = job.companyName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Job title
                Text(
                    text = job.jobTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoolGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location + salary row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!job.location.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = CoolGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = job.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = CoolGray,
                            modifier = Modifier.padding(start = 2.dp, end = 8.dp),
                            maxLines = 1
                        )
                    }
                    if (!job.salary.isNullOrBlank()) {
                        Text(
                            text = job.salary,
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Deadline
                if (job.deadline != null && job.deadline > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = CoralPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = formatDeadline(job.deadline),
                            style = MaterialTheme.typography.labelSmall,
                            color = CoralPink,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // Status badge
            Column(
                horizontalAlignment = Alignment.End
            ) {
                StatusBadge(status = job.status)

                // Date
                Text(
                    text = formatDate(job.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = GlassWhiteLight.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "New" -> Triple(SoftLavender.copy(alpha = 0.2f), SoftLavender, "Saved")
        "Applied" -> Triple(iOSGreen.copy(alpha = 0.2f), iOSGreen, "Applied")
        "Interviewing" -> Triple(iOSOrange.copy(alpha = 0.2f), iOSOrange, "Interview")
        "Offer" -> Triple(VibrantMint.copy(alpha = 0.2f), VibrantMint, "Offer")
        "Rejected" -> Triple(CoralPink.copy(alpha = 0.2f), CoralPink, "Closed")
        "Archived" -> Triple(CoolGray.copy(alpha = 0.2f), CoolGray, "Archived")
        else -> Triple(GlassWhiteLight.copy(alpha = 0.1f), CoolGray, status)
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .glassBackground(
                alpha = 0.1f,
                cornerRadius = 12.dp
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDeadline(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffDays = (timestamp - now) / (1000 * 60 * 60 * 24)

    return when {
        diffDays < 0 -> "Overdue"
        diffDays == 0L -> "Today"
        diffDays == 1L -> "Tomorrow"
        diffDays <= 7 -> "In $diffDays days"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
