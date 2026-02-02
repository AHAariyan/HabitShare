package com.habit.habitshare.presentation.screens.calendar

import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.domain.model.CheckInStatus
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val habitId: Long = 0,
    val habitTitle: String = "",
    val currentMonth: YearMonth = YearMonth.now(),
    val checkIns: Map<LocalDate, CheckIn> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val streak: Int = 0,
    val overallPercentage: Float = 0f,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class DayInfo(
    val date: LocalDate,
    val status: CheckInStatus?,
    val hasComment: Boolean,
    val isToday: Boolean,
    val isCurrentMonth: Boolean
)
