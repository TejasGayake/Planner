package com.jobtracker.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.jobtracker.ui.components.BlobBackground
import com.jobtracker.ui.components.GlassButton
import com.jobtracker.ui.components.GlassCard
import com.jobtracker.ui.theme.CoralPink
import com.jobtracker.ui.theme.GlassUltraLight
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.theme.TextPrimary
import com.jobtracker.ui.theme.TextSecondary
import com.jobtracker.ui.theme.VibrantMint
import com.jobtracker.ui.theme.iOSGreen
import com.jobtracker.ui.theme.iOSOrange
import com.jobtracker.ui.theme.iOSRed
import com.jobtracker.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val defaultReminderMinutes by viewModel.defaultReminderMinutes.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        BlobBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Notifications card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Notifications",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    SettingRow(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Get reminded about deadlines and interviews",
                        trailing = {
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { viewModel.toggleNotifications(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GlassWhite,
                                    checkedTrackColor = SoftLavender.copy(alpha = 0.6f),
                                    uncheckedThumbColor = GlassWhiteLight,
                                    uncheckedTrackColor = GlassUltraLight
                                )
                            )
                        }
                    )
                }

                // Default Reminder Time card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Default Reminder",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    Text(
                        text = "Remind me $defaultReminderMinutes minutes before",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = SoftLavender,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Slider(
                            value = defaultReminderMinutes.toFloat(),
                            onValueChange = { viewModel.setDefaultReminderMinutes(it.toInt()) },
                            valueRange = 5f..1440f,
                            steps = 18, // 5, 10, 15, 30, 60, 120, 180, 360, 720, 1440
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = SoftLavender,
                                activeTrackColor = SoftLavender.copy(alpha = 0.6f),
                                inactiveTrackColor = GlassUltraLight
                            )
                        )
                    }

                    Text(
                        text = formatReminderTime(defaultReminderMinutes),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                // Theme card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Appearance",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    SettingRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Theme",
                        subtitle = "Always use dark mode",
                        trailing = {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { viewModel.toggleDarkTheme(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GlassWhite,
                                    checkedTrackColor = SoftLavender.copy(alpha = 0.6f),
                                    uncheckedThumbColor = GlassWhiteLight,
                                    uncheckedTrackColor = GlassUltraLight
                                )
                            )
                        }
                    )
                }

                // Data Management card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Data Management",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Export button
                        GlassButton(
                            text = if (isExporting) "Exporting..." else "Export Data",
                            onClick = { viewModel.exportData() },
                            enabled = !isExporting,
                            fullWidth = true,
                            cornerRadius = 20.dp,
                            height = 48.dp,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )

                        // Import button
                        GlassButton(
                            text = if (isImporting) "Importing..." else "Import Data",
                            onClick = { showImportDialog = true },
                            enabled = !isImporting,
                            fullWidth = true,
                            cornerRadius = 20.dp,
                            height = 48.dp,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }

                // Danger Zone card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Danger Zone",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    GlassButton(
                        text = "Clear All Data",
                        onClick = { showClearDialog = true },
                        fullWidth = true,
                        cornerRadius = 20.dp,
                        height = 48.dp,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = CoralPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }

                // About card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "About",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    SettingRow(
                        icon = Icons.Default.Info,
                        title = "Job Tracker",
                        subtitle = "Version ${viewModel.appVersion}",
                        trailing = null
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Clear data confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = com.jobtracker.ui.theme.DarkSurface,
            titleContentColor = GlassWhite,
            textContentColor = GlassWhiteLight,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Clear All Data", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("This will permanently delete all your job entries and reminders. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData()
                    }
                ) {
                    Text("Clear Everything", color = CoralPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = GlassWhite)
                }
            }
        )
    }

    // Import dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = com.jobtracker.ui.theme.DarkSurface,
            titleContentColor = GlassWhite,
            textContentColor = GlassWhiteLight,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Import Data", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Paste the exported JSON data below:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    com.jobtracker.ui.components.GlassInput(
                        value = importText,
                        onValueChange = { importText = it },
                        label = "JSON Data",
                        singleLine = false,
                        maxLines = 8,
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        viewModel.importData(importText)
                        importText = ""
                    },
                    enabled = importText.isNotBlank()
                ) {
                    Text("Import", color = SoftLavender)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = GlassWhite)
                }
            }
        )
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)?
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
            tint = SoftLavender,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GlassWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        if (trailing != null) {
            trailing()
        }
    }
}

private fun formatReminderTime(minutes: Int): String {
    return when {
        minutes < 60 -> "$minutes minutes"
        minutes == 60 -> "1 hour"
        minutes < 1440 -> "${minutes / 60} hours ${minutes % 60} minutes"
        minutes == 1440 -> "1 day"
        else -> "${minutes / 1440} days"
    }
}
