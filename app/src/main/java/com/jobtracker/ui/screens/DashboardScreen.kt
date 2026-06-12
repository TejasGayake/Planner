package com.jobtracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobtracker.ui.components.BlobBackground
import com.jobtracker.ui.components.FilterPill
import com.jobtracker.ui.components.GlassButton
import com.jobtracker.ui.components.GlassCard
import com.jobtracker.ui.components.JobListItem
import com.jobtracker.ui.components.StatisticsCard
import com.jobtracker.ui.theme.CoralPink
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.TextSecondary
import com.jobtracker.ui.theme.VibrantMint
import com.jobtracker.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val filteredJobs by viewModel.filteredJobs.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val newCount by viewModel.newCount.collectAsState()
    val appliedCount by viewModel.appliedCount.collectAsState()
    val interviewCount by viewModel.interviewCount.collectAsState()
    val offerCount by viewModel.offerCount.collectAsState()
    val pendingReminders by viewModel.pendingReminderCount.collectAsState()

    val filters = remember {
        listOf("All", "New", "Applied", "Interviewing", "Offer", "Rejected")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated blob background
        BlobBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Job Tracker",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = GlassWhite
                            )
                            if (pendingReminders > 0) {
                                Text(
                                    text = "$pendingReminders pending reminder${if (pendingReminders != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CoralPink
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToCalendar) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = GlassWhite
                            )
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = GlassWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = SoftLavender,
                    contentColor = GlassWhite,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Job"
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Smart bar with summary
                item {
                    SmartSummaryBar(
                        totalCount = totalCount,
                        newCount = newCount,
                        appliedCount = appliedCount,
                        interviewCount = interviewCount,
                        offerCount = offerCount
                    )
                }

                // Stats Grid (2x2)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatisticsCard(
                            label = "Total",
                            count = totalCount,
                            icon = Icons.Default.BusinessCenter,
                            color = SoftLavender,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 20.dp
                        )
                        StatisticsCard(
                            label = "Saved",
                            count = newCount,
                            icon = Icons.Default.EventNote,
                            color = VibrantMint,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 20.dp
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatisticsCard(
                            label = "Applied",
                            count = appliedCount,
                            icon = Icons.Default.CheckCircle,
                            color = CoralPink,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 20.dp
                        )
                        StatisticsCard(
                            label = "Interview",
                            count = interviewCount,
                            icon = Icons.Default.DateRange,
                            color = com.jobtracker.ui.theme.WarmAmber,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 20.dp
                        )
                    }
                }

                // Filter pills
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { filter ->
                            val count = when (filter) {
                                "All" -> totalCount
                                "New" -> newCount
                                "Applied" -> appliedCount
                                "Interviewing" -> interviewCount
                                "Offer" -> offerCount
                                "Rejected" -> totalCount - newCount - appliedCount - interviewCount - offerCount
                                else -> 0
                            }
                            FilterPill(
                                text = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { viewModel.setFilter(filter) },
                                count = count
                            )
                        }
                    }
                }

                // Section header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedFilter == "All") "All Jobs" else selectedFilter,
                            style = MaterialTheme.typography.headlineMedium,
                            color = GlassWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${filteredJobs.size} job${if (filteredJobs.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                    }
                }

                // Job list
                if (filteredJobs.isEmpty()) {
                    item {
                        EmptyState(
                            onAddClick = onNavigateToAdd,
                            filter = selectedFilter
                        )
                    }
                } else {
                    items(
                        items = filteredJobs,
                        key = { it.id }
                    ) { job ->
                        JobListItem(
                            job = job,
                            onClick = { onNavigateToDetail(job.id) }
                        )
                    }
                }

                // Bottom spacer for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun SmartSummaryBar(
    totalCount: Int,
    newCount: Int,
    appliedCount: Int,
    interviewCount: Int,
    offerCount: Int
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = SoftLavender,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val summaryText = when {
                    interviewCount > 0 -> "$interviewCount interview${if (interviewCount != 1) "s" else ""} coming up"
                    appliedCount > 0 -> "$appliedCount application${if (appliedCount != 1) "s" else ""} in progress"
                    newCount > 0 -> "$newCount new job${if (newCount != 1) "s" else ""} saved"
                    totalCount == 0 -> "No jobs tracked yet"
                    else -> "$totalCount job${if (totalCount != 1) "s" else ""} total"
                }
                Text(
                    text = summaryText,
                    style = MaterialTheme.typography.titleMedium,
                    color = GlassWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onAddClick: () -> Unit,
    filter: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = 32.dp,
        title = if (filter == "All") "No jobs yet" else "No $filter jobs"
    ) {
        Text(
            text = if (filter == "All") {
                "Tap the + button to add your first job opportunity."
            } else {
                "No jobs with \"$filter\" status. Try a different filter or add a new job."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        GlassButton(
            text = "Add Job",
            onClick = onAddClick,
            fullWidth = true,
            cornerRadius = 20.dp,
            height = 48.dp
        )
    }
}
