package com.habit.habitshare.presentation.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habit.habitshare.domain.model.FrequencyType
import com.habit.habitshare.presentation.screens.frequency.FrequencyScreen
import com.habit.habitshare.presentation.screens.reminders.ReminderState
import com.habit.habitshare.presentation.screens.reminders.RemindersScreen
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation state for sub-screens
    var showFrequencyScreen by remember { mutableStateOf(false) }
    var showRemindersScreen by remember { mutableStateOf(false) }

    // Handle saved state
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    // Handle error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Show Frequency screen
    if (showFrequencyScreen) {
        FrequencyScreen(
            initialFrequencyType = uiState.frequencyType,
            initialFrequencyValue = uiState.frequencyValue,
            initialSpecificDays = uiState.specificDays,
            onNavigateBack = { type, value, days ->
                viewModel.updateFrequencyType(type)
                viewModel.updateFrequencyValue(value)
                viewModel.setSpecificDays(days)
                showFrequencyScreen = false
            }
        )
        return
    }

    // Show Reminders screen
    if (showRemindersScreen) {
        val reminderStates = uiState.reminders.map { reminder ->
            ReminderState(
                id = reminder.id,
                time = reminder.time,
                days = reminder.days
            )
        }
        RemindersScreen(
            initialReminders = reminderStates,
            onNavigateBack = { newReminders ->
                // Clear existing reminders and add new ones
                uiState.reminders.forEach { viewModel.removeReminder(it.id) }
                newReminders.forEach { reminderState ->
                    viewModel.addReminderWithData(reminderState.time, reminderState.days)
                }
                showRemindersScreen = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Habit",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    } else {
                        TextButton(onClick = viewModel::saveHabit) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HabitShareColors.Primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Habit Title field
            SimpleTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                placeholder = "Habit Title"
            )
            HorizontalDivider(color = HabitShareColors.Divider)

            // Description field
            SimpleTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                placeholder = "Description (Optional)"
            )
            HorizontalDivider(color = HabitShareColors.Divider)

            // Frequency row
            ClickableRow(
                label = "Frequency",
                value = getFrequencyDisplayText(uiState.frequencyType, uiState.frequencyValue),
                onClick = { showFrequencyScreen = true }
            )
            HorizontalDivider(color = HabitShareColors.Divider)

            // Shared With row (placeholder - not implemented)
            ClickableRow(
                label = "Shared With",
                value = "0 Friends",
                onClick = { /* Not implemented */ }
            )
            HorizontalDivider(color = HabitShareColors.Divider)

            // Reminders row
            ClickableRow(
                label = "Reminders",
                value = if (uiState.reminders.isEmpty()) "" else "${uiState.reminders.size}",
                onClick = { showRemindersScreen = true }
            )
            HorizontalDivider(color = HabitShareColors.Divider)
        }
    }
}

@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = HabitShareColors.TextPrimary
            ),
            cursorBrush = SolidColor(HabitShareColors.Primary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = HabitShareColors.TextHint,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun ClickableRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = HabitShareColors.TextPrimary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = HabitShareColors.TextSecondary
        )
    }
}

private fun getFrequencyDisplayText(type: FrequencyType, value: Int): String {
    return when (type) {
        FrequencyType.DAILY -> "Daily"
        FrequencyType.SPECIFIC_DAYS -> "Specific Days"
        FrequencyType.PER_WEEK -> "$value Per Week"
        FrequencyType.PER_MONTH -> "$value Per Month"
    }
}
