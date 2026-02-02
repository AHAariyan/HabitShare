package com.habit.habitshare.presentation.screens.frequency

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.habit.habitshare.domain.model.FrequencyType
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyScreen(
    initialFrequencyType: FrequencyType,
    initialFrequencyValue: Int,
    initialSpecificDays: Set<DayOfWeek>,
    onNavigateBack: (FrequencyType, Int, Set<DayOfWeek>) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialFrequencyType) }
    var frequencyValue by remember { mutableIntStateOf(initialFrequencyValue) }
    var specificDays by remember { mutableStateOf(initialSpecificDays) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Frequency",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(selectedType, frequencyValue, specificDays) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HabitShareColors.Primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Daily option
            FrequencyOptionRow(
                title = "Daily",
                isSelected = selectedType == FrequencyType.DAILY,
                onClick = { selectedType = FrequencyType.DAILY }
            )
            HorizontalDivider(color = HabitShareColors.Divider)

            // Specific Days option
            FrequencyOptionRow(
                title = "Specific Days",
                isSelected = selectedType == FrequencyType.SPECIFIC_DAYS,
                onClick = {
                    selectedType = FrequencyType.SPECIFIC_DAYS
                    if (specificDays.isEmpty()) {
                        specificDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
                    }
                }
            )
            if (selectedType == FrequencyType.SPECIFIC_DAYS) {
                DayPickerRow(
                    selectedDays = specificDays,
                    onDayToggle = { day ->
                        specificDays = if (day in specificDays) {
                            specificDays - day
                        } else {
                            specificDays + day
                        }
                    }
                )
            }
            HorizontalDivider(color = HabitShareColors.Divider)

            // Per Week option
            FrequencyOptionRow(
                title = "# Per Week",
                isSelected = selectedType == FrequencyType.PER_WEEK,
                onClick = {
                    selectedType = FrequencyType.PER_WEEK
                    if (frequencyValue < 1 || frequencyValue > 7) {
                        frequencyValue = 3
                    }
                }
            )
            if (selectedType == FrequencyType.PER_WEEK) {
                NumberDropdownRow(
                    value = frequencyValue,
                    range = 1..7,
                    onValueChange = { frequencyValue = it }
                )
            }
            HorizontalDivider(color = HabitShareColors.Divider)

            // Per Month option
            FrequencyOptionRow(
                title = "# Per Month",
                isSelected = selectedType == FrequencyType.PER_MONTH,
                onClick = {
                    selectedType = FrequencyType.PER_MONTH
                    if (frequencyValue < 1 || frequencyValue > 31) {
                        frequencyValue = 15
                    }
                }
            )
            if (selectedType == FrequencyType.PER_MONTH) {
                NumberDropdownRow(
                    value = frequencyValue,
                    range = 1..31,
                    onValueChange = { frequencyValue = it }
                )
            }
            HorizontalDivider(color = HabitShareColors.Divider)
        }
    }
}

@Composable
private fun FrequencyOptionRow(
    title: String,
    isSelected: Boolean,
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
            text = title,
            fontSize = 16.sp,
            color = HabitShareColors.TextPrimary
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = HabitShareColors.TextPrimary
            )
        }
    }
}

@Composable
private fun DayPickerRow(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { (day, label) ->
            val isSelected = day in selectedDays
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

@Composable
private fun NumberDropdownRow(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.toString(),
                fontSize = 16.sp,
                color = HabitShareColors.TextPrimary
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select",
                tint = HabitShareColors.TextSecondary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            range.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = {
                        onValueChange(number)
                        expanded = false
                    }
                )
            }
        }
    }
}
