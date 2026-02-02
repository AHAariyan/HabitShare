package com.habit.habitshare.presentation.screens.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.domain.repository.CheckInRepository
import com.habit.habitshare.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

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

                val streak = checkInRepository.calculateStreak(habitId)
                val percentage = checkInRepository.calculateOverallPercentage(habitId)

                _uiState.update {
                    it.copy(
                        habitId = habitId,
                        habitTitle = habit.title,
                        streak = streak,
                        overallPercentage = percentage,
                        isLoading = false
                    )
                }

                loadMonthData(_uiState.value.currentMonth)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load habit")
                }
            }
        }
    }

    private suspend fun loadMonthData(yearMonth: YearMonth) {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        val checkIns = checkInRepository.getCheckInsForHabitInRangeSync(habitId, startDate, endDate)
        val checkInMap = checkIns.associateBy { it.date }

        _uiState.update {
            it.copy(checkIns = checkInMap)
        }
    }

    fun previousMonth() {
        viewModelScope.launch {
            val newMonth = _uiState.value.currentMonth.minusMonths(1)
            _uiState.update { it.copy(currentMonth = newMonth, selectedDate = null) }
            loadMonthData(newMonth)
        }
    }

    fun nextMonth() {
        viewModelScope.launch {
            val newMonth = _uiState.value.currentMonth.plusMonths(1)
            _uiState.update { it.copy(currentMonth = newMonth, selectedDate = null) }
            loadMonthData(newMonth)
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = if (it.selectedDate == date) null else date)
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val streak = checkInRepository.calculateStreak(habitId)
            val percentage = checkInRepository.calculateOverallPercentage(habitId)

            _uiState.update {
                it.copy(streak = streak, overallPercentage = percentage)
            }

            loadMonthData(_uiState.value.currentMonth)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
