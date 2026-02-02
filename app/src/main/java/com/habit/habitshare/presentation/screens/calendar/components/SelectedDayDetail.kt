package com.habit.habitshare.presentation.screens.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SelectedDayDetail(
    date: LocalDate,
    checkIn: CheckIn?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(onClick = onEditClick) {
                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (checkIn != null) {
                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val (statusIcon, statusText, statusColor) = when (checkIn.status) {
                        CheckInStatus.SUCCESS -> Triple("✓", "Success", HabitShareColors.Success)
                        CheckInStatus.FAIL -> Triple("✗", "Failed", HabitShareColors.Fail)
                        CheckInStatus.SKIP -> Triple("−", "Skipped", HabitShareColors.Skip)
                    }

                    Text(
                        text = statusIcon,
                        color = statusColor,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }

                // Comment
                if (!checkIn.comment.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Notes:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = checkIn.comment,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "No check-in for this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
