package com.habit.habitshare.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.domain.repository.CheckInRepository
import com.habit.habitshare.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _quickActionResult = MutableStateFlow<QuickActionResult?>(null)
    val quickActionResult: StateFlow<QuickActionResult?> = _quickActionResult.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            habitRepository.getAllHabits()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
                .collect { habits ->
                    val habitsWithStats = habits.map { habit ->
                        val streak = checkInRepository.calculateStreak(habit.id)
                        val percentage = checkInRepository.calculateOverallPercentage(habit.id)
                        val weekCheckIns = getWeekCheckIns(habit.id)

                        HabitWithStats(
                            id = habit.id,
                            title = habit.title,
                            isPrivate = habit.isPrivate,
                            streak = streak,
                            overallPercentage = percentage,
                            weekCheckIns = weekCheckIns
                        )
                    }

                    _uiState.update {
                        it.copy(
                            habits = habitsWithStats,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private suspend fun getWeekCheckIns(habitId: Long): Map<LocalDate, CheckInStatus> {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = startOfWeek.plusDays(6)

        val checkIns = checkInRepository.getCheckInsForHabitInRangeSync(habitId, startOfWeek, endOfWeek)
        return checkIns.associate { it.date to it.status }
    }

    fun quickCheckIn(habitId: Long, date: LocalDate, status: CheckInStatus) {
        viewModelScope.launch {
            try {
                val existingCheckIn = checkInRepository.getCheckInForDateSync(habitId, date)

                val checkIn = CheckIn(
                    id = existingCheckIn?.id ?: 0,
                    habitId = habitId,
                    date = date,
                    status = status,
                    comment = existingCheckIn?.comment
                )

                checkInRepository.logCheckIn(checkIn)
                _quickActionResult.value = QuickActionResult.Success(habitId, date, status)

                // Refresh habits to update stats
                refreshHabit(habitId)
            } catch (e: Exception) {
                _quickActionResult.value = QuickActionResult.Error(e.message ?: "Failed to save check-in")
            }
        }
    }

    fun clearCheckIn(habitId: Long, date: LocalDate) {
        viewModelScope.launch {
            try {
                checkInRepository.deleteCheckIn(habitId, date)
                _quickActionResult.value = QuickActionResult.Cleared(habitId, date)

                // Refresh habits to update stats
                refreshHabit(habitId)
            } catch (e: Exception) {
                _quickActionResult.value = QuickActionResult.Error(e.message ?: "Failed to clear check-in")
            }
        }
    }

    private suspend fun refreshHabit(habitId: Long) {
        val habit = habitRepository.getHabitByIdSync(habitId) ?: return
        val streak = checkInRepository.calculateStreak(habitId)
        val percentage = checkInRepository.calculateOverallPercentage(habitId)
        val weekCheckIns = getWeekCheckIns(habitId)

        val updatedHabitWithStats = HabitWithStats(
            id = habit.id,
            title = habit.title,
            isPrivate = habit.isPrivate,
            streak = streak,
            overallPercentage = percentage,
            weekCheckIns = weekCheckIns
        )

        _uiState.update { state ->
            state.copy(
                habits = state.habits.map {
                    if (it.id == habitId) updatedHabitWithStats else it
                }
            )
        }
    }

    fun clearQuickActionResult() {
        _quickActionResult.value = null
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
