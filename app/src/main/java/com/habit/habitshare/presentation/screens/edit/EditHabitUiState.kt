package com.habit.habitshare.presentation.screens.edit

import com.habit.habitshare.domain.model.FrequencyType
import java.time.DayOfWeek
import java.time.LocalTime

data class EditHabitUiState(
    val habitId: Long = 0,
    val title: String = "",
    val description: String = "",
    val frequencyType: FrequencyType = FrequencyType.DAILY,
    val frequencyValue: Int = 1,
    val specificDays: Set<DayOfWeek> = emptySet(),
    val isPrivate: Boolean = true,
    val reminders: List<EditReminderUiState> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && when (frequencyType) {
            FrequencyType.DAILY -> true
            FrequencyType.SPECIFIC_DAYS -> specificDays.isNotEmpty()
            FrequencyType.PER_WEEK -> frequencyValue in 1..7
            FrequencyType.PER_MONTH -> frequencyValue in 1..31
        }
}

data class EditReminderUiState(
    val id: Long = 0,
    val time: LocalTime = LocalTime.of(8, 0),
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val isEnabled: Boolean = true,
    val isNew: Boolean = false  // Track if this is a new reminder
)
