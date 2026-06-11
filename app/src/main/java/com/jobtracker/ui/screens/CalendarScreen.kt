package com.jobtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobtracker.data.db.Job
import com.jobtracker.ui.components.BlobBackground
import com.jobtracker.ui.components.GlassCard
import com.jobtracker.ui.components.JobListItem
import com.jobtracker.ui.theme.CoralPink
import com.jobtracker.ui.theme.GlassBorder
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.VibrantMint
import com.jobtracker.ui.viewmodel.CalendarDay
import com.jobtracker.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    viewModel: CalendarViewModel = viewModel()
) {
    val calendarState by viewModel.calendarState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        BlobBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Calendar",
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
                        IconButton(onClick = { viewModel.selectToday() }) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Today",
                                tint = SoftLavender
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calendar header with month navigation
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = 16.dp
                    ) {
                        // Month navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.navigateMonth(-1) }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous month",
                                    tint = GlassWhite
                                )
                            }

                            Text(
                                text = viewModel.getMonthHeader(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = GlassWhite
                            )

                            IconButton(onClick = { viewModel.navigateMonth(1) }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next month",
                                    tint = GlassWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Day headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            viewModel.getDayHeader().forEach { dayName ->
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GlassWhiteLight.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Calendar grid
                        calendarState.days.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                week.forEach { day ->
                                    CalendarDayCell(
                                        day = day,
                                        isSelected = day.date == calendarState.selectedDate,
                                        onClick = { viewModel.selectDate(day.date) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected date jobs
                if (calendarState.selectedDate != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = calendarState.selectedDate!!.format(
                                    DateTimeFormatter.ofPattern("EEEE, MMMM d")
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                color = GlassWhite,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Find jobs for this date
                            val hasJobs = calendarState.days.flatten()
                                .firstOrNull { it.date == calendarState.selectedDate }
                                ?.hasJobs == true

                            if (hasJobs) {
                                Text(
                                    text = "●",
                                    color = SoftLavender,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    // Show jobs that have events on this date
                    val dayJobs = calendarState.days.flatten()
                        .firstOrNull { it.date == calendarState.selectedDate }
                        ?.jobs ?: emptyList()

                    if (dayJobs.isEmpty()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 20.dp,
                                contentPadding = 24.dp
                            ) {
                                Text(
                                    text = "No jobs for this date",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = GlassWhiteLight.copy(alpha = 0.5f)
                                )
                            }
                        }
                    } else {
                        items(dayJobs) { job ->
                            JobListItem(
                                job = job,
                                onClick = { onNavigateToDetail(job.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellSize: Dp = 42.dp

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .size(cellSize)
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(SoftLavender.copy(alpha = 0.4f))
                } else if (day.isToday) {
                    Modifier.border(1.dp, SoftLavender.copy(alpha = 0.6f), CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    GlassWhite
                } else if (!day.isCurrentMonth) {
                    GlassWhiteLight.copy(alpha = 0.25f)
                } else if (day.isToday) {
                    SoftLavender
                } else {
                    GlassWhite.copy(alpha = 0.8f)
                },
                textAlign = TextAlign.Center
            )

            // Job indicator dot
            if (day.hasJobs) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.hasDeadlines) CoralPink else VibrantMint
                        )
                )
            }
        }
    }
}
