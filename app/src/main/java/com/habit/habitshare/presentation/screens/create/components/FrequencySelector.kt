package com.habit.habitshare.presentation.screens.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habit.habitshare.domain.model.FrequencyType
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.DayOfWeek

@Composable
fun FrequencySelector(
    selectedType: FrequencyType,
    frequencyValue: Int,
    specificDays: Set<DayOfWeek>,
    onTypeSelected: (FrequencyType) -> Unit,
    onValueChanged: (Int) -> Unit,
    onDayToggled: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Frequency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Frequency type chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FrequencyType.entries.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HabitShareColors.Primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic content based on selected type
        when (selectedType) {
            FrequencyType.DAILY -> {
                Text(
                    text = "Habit will repeat every day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FrequencyType.SPECIFIC_DAYS -> {
                DayPicker(
                    selectedDays = specificDays,
                    onDayToggled = onDayToggled
                )
            }

            FrequencyType.PER_WEEK -> {
                NumberPicker(
                    value = frequencyValue,
                    range = 1..7,
                    onValueChanged = onValueChanged,
                    suffix = "times per week"
                )
            }

            FrequencyType.PER_MONTH -> {
                NumberPicker(
                    value = frequencyValue,
                    range = 1..31,
                    onValueChanged = onValueChanged,
                    suffix = "times per month"
                )
            }
        }
    }
}

private val FrequencyType.displayName: String
    get() = when (this) {
        FrequencyType.DAILY -> "Daily"
        FrequencyType.SPECIFIC_DAYS -> "Specific Days"
        FrequencyType.PER_WEEK -> "Per Week"
        FrequencyType.PER_MONTH -> "Per Month"
    }
