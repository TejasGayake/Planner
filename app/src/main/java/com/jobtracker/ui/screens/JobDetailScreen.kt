package com.jobtracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobtracker.data.db.Job
import com.jobtracker.ui.components.BlobBackground
import com.jobtracker.ui.components.GlassButton
import com.jobtracker.ui.components.GlassCard
import com.jobtracker.ui.components.StatusStepper
import com.jobtracker.ui.theme.CoralPink
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.VibrantMint
import com.jobtracker.ui.theme.WarmAmber
import com.jobtracker.ui.theme.iOSGreen
import com.jobtracker.ui.theme.iOSRed
import com.jobtracker.ui.viewmodel.JobDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    onNavigateBack: () -> Unit = {},
    onEditJob: (Long) -> Unit = {},
    viewModel: JobDetailViewModel = viewModel()
) {
    val job by viewModel.job.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val deleteComplete by viewModel.deleteComplete.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
        viewModel.loadReminders(jobId)
    }

    // Navigate back after delete
    LaunchedEffect(deleteComplete) {
        if (deleteComplete) onNavigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BlobBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = job?.companyName ?: "Job Details",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = GlassWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = GlassWhite
                            )
                        }
                    },
                    actions = {
                        if (job != null) {
                            IconButton(onClick = { onEditJob(job!!.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = GlassWhite
                                )
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = CoralPink
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GlassWhiteLight
                    )
                }
            } else if (job == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Job not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GlassWhiteLight
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status stepper card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Progress",
                        cornerRadius = 24.dp,
                        contentPadding = 16.dp
                    ) {
                        StatusStepper(
                            currentStatus = job!!.status,
                            onStepClick = { newStatus ->
                                viewModel.updateStatus(newStatus)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick action buttons based on current status
                        StatusActionButtons(
                            currentStatus = job!!.status,
                            onUpdateStatus = { viewModel.updateStatus(it) }
                        )
                    }

                    // Company info card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = 16.dp
                    ) {
                        // Job title
                        Text(
                            text = job!!.jobTitle,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = GlassWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Company
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BusinessCenter,
                                contentDescription = null,
                                tint = SoftLavender,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = job!!.companyName,
                                style = MaterialTheme.typography.titleLarge,
                                color = SoftLavender
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Location
                        if (!job!!.location.isNullOrBlank()) {
                            DetailRow(
                                icon = Icons.Default.LocationOn,
                                label = job!!.location,
                                iconTint = WarmAmber
                            )
                        }

                        // Salary
                        if (!job!!.salary.isNullOrBlank()) {
                            DetailRow(
                                icon = Icons.Default.Work,
                                label = job!!.salary,
                                iconTint = VibrantMint
                            )
                        }

                        // Job Type
                        if (!job!!.jobType.isNullOrBlank()) {
                            DetailRow(
                                icon = Icons.Default.Schedule,
                                label = job!!.jobType!!,
                                iconTint = SoftLavender
                            )
                        }

                        // Source
                        DetailRow(
                            icon = Icons.Default.Email,
                            label = "via ${job!!.source}",
                            iconTint = GlassWhiteLight
                        )
                    }

                    // Dates card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Dates",
                        cornerRadius = 24.dp,
                        contentPadding = 16.dp
                    ) {
                        DetailRow(
                            icon = Icons.Default.CalendarMonth,
                            label = "Created: ${formatDate(job!!.createdAt)}",
                            iconTint = SoftLavender
                        )
                        if (job!!.appliedDate != null) {
                            DetailRow(
                                icon = Icons.Default.CheckCircle,
                                label = "Applied: ${formatDate(job!!.appliedDate!!)}",
                                iconTint = iOSGreen
                            )
                        }
                        if (job!!.interviewDate != null) {
                            DetailRow(
                                icon = Icons.Default.Schedule,
                                label = "Interview: ${formatDate(job!!.interviewDate!!)}",
                                iconTint = WarmAmber
                            )
                        }
                        if (job!!.deadline != null && job!!.deadline!! > 0) {
                            DetailRow(
                                icon = Icons.Default.AccessTime,
                                label = "Deadline: ${formatDate(job!!.deadline!!)}",
                                iconTint = CoralPink
                            )
                        }
                    }

                    // Notes card
                    if (!job!!.notes.isNullOrBlank()) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Notes",
                            cornerRadius = 24.dp,
                            contentPadding = 16.dp
                        ) {
                            Text(
                                text = job!!.notes!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = GlassWhite.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Reminders card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Reminders",
                        cornerRadius = 24.dp,
                        contentPadding = 16.dp
                    ) {
                        if (reminders.isEmpty()) {
                            Text(
                                text = "No reminders set",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassWhiteLight.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        } else {
                            reminders.forEach { reminder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (reminder.isCompleted) iOSGreen else WarmAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reminder.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GlassWhite
                                        )
                                        Text(
                                            text = formatDate(reminder.remindAt),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GlassWhiteLight.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GlassButton(
                            text = "Set Reminder",
                            onClick = { showReminderDialog = true },
                            fullWidth = true,
                            cornerRadius = 20.dp,
                            height = 44.dp,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }

                    // Delete button at bottom
                    GlassButton(
                        text = "Delete Job",
                        onClick = { showDeleteDialog = true },
                        fullWidth = true,
                        cornerRadius = 24.dp,
                        height = 52.dp,
                        modifier = Modifier.padding(bottom = 32.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = CoralPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = com.jobtracker.ui.theme.DarkSurface,
            titleContentColor = GlassWhite,
            textContentColor = GlassWhiteLight,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Delete Job", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete this job entry? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteJob()
                    }
                ) {
                    Text("Delete", color = CoralPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = GlassWhite)
                }
            }
        )
    }
}

@Composable
private fun StatusActionButtons(
    currentStatus: String,
    onUpdateStatus: (String) -> Unit
) {
    when (currentStatus) {
        "New" -> {
            GlassButton(
                text = "Mark as Applied",
                onClick = { onUpdateStatus("Applied") },
                fullWidth = true,
                cornerRadius = 20.dp,
                height = 44.dp
            )
        }
        "Applied" -> {
            GlassButton(
                text = "Mark Interview Scheduled",
                onClick = { onUpdateStatus("Interviewing") },
                fullWidth = true,
                cornerRadius = 20.dp,
                height = 44.dp
            )
        }
        "Interviewing" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassButton(
                    text = "Got Offer",
                    onClick = { onUpdateStatus("Offer") },
                    modifier = Modifier.weight(1f),
                    cornerRadius = 20.dp,
                    height = 44.dp
                )
                GlassButton(
                    text = "Rejected",
                    onClick = { onUpdateStatus("Rejected") },
                    modifier = Modifier.weight(1f),
                    cornerRadius = 20.dp,
                    height = 44.dp
                )
            }
        }
        "Offer" -> {
            GlassButton(
                text = "Mark as Accepted",
                onClick = { onUpdateStatus("Archived") },
                fullWidth = true,
                cornerRadius = 20.dp,
                height = 44.dp
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color = GlassWhiteLight
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = GlassWhite.copy(alpha = 0.85f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
