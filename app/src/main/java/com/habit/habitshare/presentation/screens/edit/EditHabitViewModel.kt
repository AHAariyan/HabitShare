package com.habit.habitshare.presentation.screens.edit

import androidx.lifecycle.SavedStateHandle
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
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0

    private val _uiState = MutableStateFlow(EditHabitUiState())
    val uiState: StateFlow<EditHabitUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            try {
                val habit = habitRepository.getHabitByIdSync(habitId)

                if (habit == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Habit not found")
                    }
                    return@launch
                }

                val reminders = habitRepository.getRemindersForHabit(habitId).first()

                _uiState.update {
                    it.copy(
                        habitId = habit.id,
                        title = habit.title,
                        description = habit.description ?: "",
                        frequencyType = habit.frequencyType,
                        frequencyValue = habit.frequencyValue ?: 1,
                        specificDays = habit.specificDays,
                        isPrivate = habit.isPrivate,
                        reminders = reminders.map { reminder ->
                            EditReminderUiState(
                                id = reminder.id,
                                time = reminder.time,
                                days = reminder.days,
                                isEnabled = reminder.isEnabled,
                                isNew = false
                            )
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load habit")
                }
            }
        }
    }

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
                    FrequencyType.PER_WEEK -> if (it.frequencyType == FrequencyType.PER_WEEK) it.frequencyValue else 3
                    FrequencyType.PER_MONTH -> if (it.frequencyType == FrequencyType.PER_MONTH) it.frequencyValue else 15
                    else -> 1
                },
                specificDays = if (frequencyType == FrequencyType.SPECIFIC_DAYS && it.specificDays.isEmpty()) {
                    setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
                } else it.specificDays
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
            state.copy(
                reminders = state.reminders + EditReminderUiState(
                    id = System.currentTimeMillis(),
                    isNew = true
                )
            )
        }
    }

    fun addReminderWithData(time: LocalTime, days: Set<DayOfWeek>) {
        _uiState.update { state ->
            state.copy(
                reminders = state.reminders + EditReminderUiState(
                    id = System.currentTimeMillis(),
                    time = time,
                    days = days,
                    isNew = true
                )
            )
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
                val existingHabit = habitRepository.getHabitByIdSync(habitId)

                val habit = Habit(
                    id = habitId,
                    title = state.title.trim(),
                    description = state.description.trim().takeIf { it.isNotEmpty() },
                    frequencyType = state.frequencyType,
                    frequencyValue = when (state.frequencyType) {
                        FrequencyType.PER_WEEK, FrequencyType.PER_MONTH -> state.frequencyValue
                        else -> null
                    },
                    specificDays = state.specificDays,
                    isPrivate = state.isPrivate,
                    createdAt = existingHabit?.createdAt ?: LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                habitRepository.updateHabit(habit)

                // Cancel all existing reminders
                val existingReminders = habitRepository.getRemindersForHabit(habitId).first()
                existingReminders.forEach { reminder ->
                    reminderScheduler.cancelReminder(reminder)
                    habitRepository.deleteReminder(reminder.id)
                }

                // Add new reminders and schedule them
                state.reminders.forEach { reminderState ->
                    val reminder = Reminder(
                        id = 0, // New ID will be generated
                        habitId = habitId,
                        time = reminderState.time,
                        days = reminderState.days,
                        isEnabled = reminderState.isEnabled
                    )
                    val reminderId = habitRepository.addReminder(reminder)

                    // Schedule the new reminder
                    val savedReminder = reminder.copy(id = reminderId)
                    if (savedReminder.isEnabled) {
                        reminderScheduler.scheduleReminder(savedReminder, state.title.trim())
                    }
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

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteHabit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showDeleteConfirmation = false) }

            try {
                // Cancel all reminders before deleting
                val reminders = habitRepository.getRemindersForHabit(habitId).first()
                reminders.forEach { reminder ->
                    reminderScheduler.cancelReminder(reminder)
                }

                habitRepository.deleteHabit(habitId)
                _uiState.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to delete habit"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
