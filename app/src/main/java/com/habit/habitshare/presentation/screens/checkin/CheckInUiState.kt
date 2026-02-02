package com.habit.habitshare.presentation.screens.checkin

import com.habit.habitshare.domain.model.CheckInStatus
import java.time.LocalDate

data class CheckInUiState(
    val habitId: Long = 0,
    val habitTitle: String = "",
    val date: LocalDate = LocalDate.now(),
    val status: CheckInStatus? = null,
    val comment: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
