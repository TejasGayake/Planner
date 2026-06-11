package com.jobtracker.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobtracker.ui.components.BlobBackground
import com.jobtracker.ui.components.GlassButton
import com.jobtracker.ui.components.GlassCard
import com.jobtracker.ui.components.GlassInput
import com.jobtracker.ui.components.glassBackground
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight
import com.jobtracker.ui.theme.SoftLavender
import com.jobtracker.ui.viewmodel.AddEditJobViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobScreen(
    jobId: Long? = null,
    onNavigateBack: () -> Unit = {},
    onJobSaved: (Long) -> Unit = {},
    viewModel: AddEditJobViewModel = viewModel()
) {
    val companyName by viewModel.companyName.collectAsState()
    val jobTitle by viewModel.jobTitle.collectAsState()
    val location by viewModel.location.collectAsState()
    val salary by viewModel.salary.collectAsState()
    val jobType by viewModel.jobType.collectAsState()
    val source by viewModel.source.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val deadline by viewModel.deadline.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveComplete by viewModel.saveComplete.collectAsState()
    val savedJobId by viewModel.savedJobId.collectAsState()
    val companyError by viewModel.companyError.collectAsState()
    val titleError by viewModel.titleError.collectAsState()

    val context = LocalContext.current

    // Load job if editing
    if (jobId != null && !isEditing && !saveComplete) {
        viewModel.loadJob(jobId)
    }

    // Handle save completion
    if (saveComplete && savedJobId != null) {
        val savedId = savedJobId!!
        viewModel.reset()
        onJobSaved(savedId)
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BlobBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEditing) "Edit Job" else "Add Job",
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
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                // Company Info Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Company Details",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    GlassInput(
                        value = companyName,
                        onValueChange = { viewModel.updateCompanyName(it) },
                        label = "Company Name",
                        placeholder = "e.g. Google, Stripe, Acme Corp",
                        error = companyError,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    GlassInput(
                        value = jobTitle,
                        onValueChange = { viewModel.updateJobTitle(it) },
                        label = "Job Title",
                        placeholder = "e.g. Senior Android Developer",
                        error = titleError,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    GlassInput(
                        value = location,
                        onValueChange = { viewModel.updateLocation(it) },
                        label = "Location",
                        placeholder = "e.g. San Francisco, CA / Remote"
                    )
                }

                // Details Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Job Details",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    GlassInput(
                        value = salary,
                        onValueChange = { viewModel.updateSalary(it) },
                        label = "Salary",
                        placeholder = "e.g. $120k - $180k",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Job Type dropdown
                    TypeDropdown(
                        label = "Job Type",
                        selectedValue = jobType,
                        options = viewModel.jobTypes,
                        onValueChange = { viewModel.updateJobType(it) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Source dropdown
                    TypeDropdown(
                        label = "Source",
                        selectedValue = source,
                        options = viewModel.sources,
                        onValueChange = { viewModel.updateSource(it) }
                    )
                }

                // Deadline Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Deadline",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    DeadlinePicker(
                        deadline = deadline,
                        onDeadlineSelected = { viewModel.updateDeadline(it) },
                        onClear = { viewModel.updateDeadline(null) }
                    )
                }

                // Notes Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Notes",
                    cornerRadius = 24.dp,
                    contentPadding = 16.dp
                ) {
                    GlassInput(
                        value = notes,
                        onValueChange = { viewModel.updateNotes(it) },
                        label = "Notes",
                        placeholder = "Job description, contacts, links...",
                        singleLine = false,
                        maxLines = 6,
                        minLines = 3
                    )
                }

                // Save button
                GlassButton(
                    text = if (isSaving) "Saving..." else if (isEditing) "Update Job" else "Save Job",
                    onClick = { viewModel.save() },
                    enabled = !isSaving,
                    fullWidth = true,
                    cornerRadius = 24.dp,
                    height = 56.dp,
                    icon = {
                        if (!isSaving) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        GlassInput(
            value = selectedValue,
            onValueChange = {},
            label = label,
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .glassBackground(cornerRadius = 16.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (option == selectedValue) SoftLavender else GlassWhite
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DeadlinePicker(
    deadline: Long?,
    onDeadlineSelected: (Long) -> Unit,
    onClear: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    if (deadline != null) {
        calendar.timeInMillis = deadline
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (deadline != null) {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(deadline))
                } else {
                    "No deadline set"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = if (deadline != null) GlassWhite else GlassWhiteLight.copy(alpha = 0.5f)
            )
        }

        Row {
            androidx.compose.material3.TextButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val cal = Calendar.getInstance()
                        cal.set(year, month, dayOfMonth)
                        onDeadlineSelected(cal.timeInMillis)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Pick date",
                    tint = SoftLavender
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pick Date",
                    color = SoftLavender
                )
            }

            if (deadline != null) {
                androidx.compose.material3.TextButton(onClick = onClear) {
                    Text(
                        text = "Clear",
                        color = com.jobtracker.ui.theme.iOSRed
                    )
                }
            }
        }
    }
}

// menuAnchor() is provided by ExposedDropdownMenuBoxScope within the dropdown box
