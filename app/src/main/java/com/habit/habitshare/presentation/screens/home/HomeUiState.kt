package com.habit.habitshare.presentation.screens.home

import com.habit.habitshare.domain.model.CheckInStatus
import java.time.LocalDate

data class HomeUiState(
    val habits: List<HabitWithStats> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class HabitWithStats(
    val id: Long,
    val title: String,
    val isPrivate: Boolean,
    val streak: Int,
    val overallPercentage: Float,
    val weekCheckIns: Map<LocalDate, CheckInStatus>
)

sealed class QuickActionResult {
    data class Success(val habitId: Long, val date: LocalDate, val status: CheckInStatus) : QuickActionResult()
    data class Cleared(val habitId: Long, val date: LocalDate) : QuickActionResult()
    data class Error(val message: String) : QuickActionResult()
}
