package com.habit.habitshare.presentation.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.habitshare.domain.model.FrequencyType
import com.habit.habitshare.domain.model.Habit
import com.habit.habitshare.domain.model.Reminder
import com.habit.habitshare.domain.repository.HabitRepository
import com.habit.habitshare.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState: StateFlow<CreateHabitUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, errorMessage = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateFrequencyType(frequencyType: FrequencyType) {
        _uiState.update {
            it.copy(
                frequencyType = frequencyType,
                frequencyValue = when (frequencyType) {
                    FrequencyType.PER_WEEK -> 3
                    FrequencyType.PER_MONTH -> 15
                    else -> 1
                },
                specificDays = if (frequencyType == FrequencyType.SPECIFIC_DAYS) {
                    setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
                } else emptySet()
            )
        }
    }

    fun updateFrequencyValue(value: Int) {
        _uiState.update { it.copy(frequencyValue = value) }
    }

    fun toggleSpecificDay(day: DayOfWeek) {
        _uiState.update { state ->
            val newDays = if (day in state.specificDays) {
                state.specificDays - day
            } else {
                state.specificDays + day
            }
            state.copy(specificDays = newDays)
        }
    }

    fun updatePrivacy(isPrivate: Boolean) {
        _uiState.update { it.copy(isPrivate = isPrivate) }
    }

    fun addReminder() {
        _uiState.update { state ->
            state.copy(reminders = state.reminders + ReminderUiState())
        }
    }

    fun addReminderWithData(time: LocalTime, days: Set<DayOfWeek>) {
        _uiState.update { state ->
            state.copy(reminders = state.reminders + ReminderUiState(
                id = System.currentTimeMillis(),
                time = time,
                days = days
            ))
        }
    }

    fun setSpecificDays(days: Set<DayOfWeek>) {
        _uiState.update { it.copy(specificDays = days) }
    }

    fun removeReminder(reminderId: Long) {
        _uiState.update { state ->
            state.copy(reminders = state.reminders.filter { it.id != reminderId })
        }
    }

    fun updateReminderTime(reminderId: Long, time: LocalTime) {
        _uiState.update { state ->
            state.copy(
                reminders = state.reminders.map { reminder ->
                    if (reminder.id == reminderId) reminder.copy(time = time)
                    else reminder
                }
            )
        }
    }

    fun toggleReminderDay(reminderId: Long, day: DayOfWeek) {
        _uiState.update { state ->
            state.copy(
                reminders = state.reminders.map { reminder ->
                    if (reminder.id == reminderId) {
                        val newDays = if (day in reminder.days) {
                            reminder.days - day
                        } else {
                            reminder.days + day
                        }
                        reminder.copy(days = newDays)
                    } else reminder
                }
            )
        }
    }

    fun saveHabit() {
        val state = _uiState.value

        if (!state.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val habit = Habit(
                    title = state.title.trim(),
                    description = state.description.trim().takeIf { it.isNotEmpty() },
                    frequencyType = state.frequencyType,
                    frequencyValue = when (state.frequencyType) {
                        FrequencyType.PER_WEEK, FrequencyType.PER_MONTH -> state.frequencyValue
                        else -> null
                    },
                    specificDays = state.specificDays,
                    isPrivate = state.isPrivate
                )

                val reminders = state.reminders.map { reminderState ->
                    Reminder(
                        habitId = 0, // Will be set by repository
                        time = reminderState.time,
                        days = reminderState.days,
                        isEnabled = reminderState.isEnabled
                    )
                }

                val habitId = habitRepository.createHabit(habit, reminders)

                // Schedule reminders for the new habit
                val savedReminders = habitRepository.getRemindersForHabit(habitId).first()
                savedReminders.forEach { reminder ->
                    reminderScheduler.scheduleReminder(reminder, state.title.trim())
                }

                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to save habit"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
