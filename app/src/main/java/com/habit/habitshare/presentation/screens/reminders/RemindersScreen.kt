package com.habit.habitshare.presentation.screens.reminders

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ReminderState(
    val id: Long = System.currentTimeMillis(),
    val time: LocalTime = LocalTime.of(9, 0),
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    initialReminders: List<ReminderState>,
    onNavigateBack: (List<ReminderState>) -> Unit
) {
    var reminders by remember { mutableStateOf(initialReminders) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingReminderId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reminders",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(reminders) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            reminders = reminders + ReminderState()
                        }
                    ) {
                        Text(
                            text = "Add",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HabitShareColors.Primary
                )
            )
        }
    ) { paddingValues ->
        if (reminders.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(80.dp),
                    tint = HabitShareColors.PrimaryLight.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onTimeClick = {
                            editingReminderId = reminder.id
                            showTimePicker = true
                        },
                        onDayToggle = { day ->
                            reminders = reminders.map {
                                if (it.id == reminder.id) {
                                    val newDays = if (day in it.days) it.days - day else it.days + day
                                    it.copy(days = newDays)
                                } else it
                            }
                        },
                        onDelete = {
                            reminders = reminders.filter { it.id != reminder.id }
                        }
                    )
                    HorizontalDivider(color = HabitShareColors.Divider)
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker && editingReminderId != null) {
        val currentReminder = reminders.find { it.id == editingReminderId }
        if (currentReminder != null) {
            TimePickerDialog(
                initialTime = currentReminder.time,
                onConfirm = { newTime ->
                    reminders = reminders.map {
                        if (it.id == editingReminderId) it.copy(time = newTime) else it
                    }
                    showTimePicker = false
                    editingReminderId = null
                },
                onDismiss = {
                    showTimePicker = false
                    editingReminderId = null
                }
            )
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: ReminderState,
    onTimeClick: () -> Unit,
    onDayToggle: (DayOfWeek) -> Unit,
    onDelete: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reminder.time.format(timeFormatter),
                fontSize = 18.sp,
                color = HabitShareColors.TextPrimary,
                modifier = Modifier.clickable(onClick = onTimeClick)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = HabitShareColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day picker row
        val days = listOf(
            DayOfWeek.SUNDAY to "S",
            DayOfWeek.MONDAY to "M",
            DayOfWeek.TUESDAY to "T",
            DayOfWeek.WEDNESDAY to "W",
            DayOfWeek.THURSDAY to "T",
            DayOfWeek.FRIDAY to "F",
            DayOfWeek.SATURDAY to "S"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { (day, label) ->
                val isSelected = day in reminder.days
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) HabitShareColors.Success
                            else HabitShareColors.DayUnselected
                        )
                        .clickable { onDayToggle(day) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else HabitShareColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimePicker(state = timePickerState)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL")
                }
                TextButton(
                    onClick = {
                        onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}
