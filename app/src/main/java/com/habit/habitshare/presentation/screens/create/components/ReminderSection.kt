package com.habit.habitshare.presentation.screens.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habit.habitshare.presentation.screens.create.ReminderUiState
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun ReminderSection(
    reminders: List<ReminderUiState>,
    onAddReminder: () -> Unit,
    onRemoveReminder: (Long) -> Unit,
    onTimeChanged: (Long, LocalTime) -> Unit,
    onDayToggled: (Long, DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminders",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(onClick = onAddReminder) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (reminders.isEmpty()) {
            Text(
                text = "No reminders set. Tap Add to create one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reminders.forEach { reminder ->
                ReminderItem(
                    reminder = reminder,
                    onRemove = { onRemoveReminder(reminder.id) },
                    onTimeChanged = { time -> onTimeChanged(reminder.id, time) },
                    onDayToggled = { day -> onDayToggled(reminder.id, day) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
