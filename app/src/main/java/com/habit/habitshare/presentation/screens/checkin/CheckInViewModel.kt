package com.habit.habitshare.presentation.screens.checkin

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0
    private val dateString: String = savedStateHandle.get<String>("date") ?: ""

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private var existingCheckInId: Long = 0

    init {
        loadCheckIn()
    }

    private fun loadCheckIn() {
        viewModelScope.launch {
            try {
                val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
                val habit = habitRepository.getHabitByIdSync(habitId)

                if (habit == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Habit not found")
                    }
                    return@launch
                }

                val existingCheckIn = checkInRepository.getCheckInForDateSync(habitId, date)

                existingCheckInId = existingCheckIn?.id ?: 0

                _uiState.update {
                    it.copy(
                        habitId = habitId,
                        habitTitle = habit.title,
                        date = date,
                        status = existingCheckIn?.status,
                        comment = existingCheckIn?.comment ?: "",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load check-in")
                }
            }
        }
    }

    fun updateStatus(status: CheckInStatus?) {
        _uiState.update { it.copy(status = status) }
    }

    fun clearStatus() {
        _uiState.update { it.copy(status = null) }
    }

    fun updateComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun saveCheckIn() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                if (state.status == null) {
                    // Clear check-in if status is null
                    if (existingCheckInId != 0L) {
                        checkInRepository.deleteCheckIn(state.habitId, state.date)
                    }
                } else {
                    val checkIn = CheckIn(
                        id = existingCheckInId,
                        habitId = state.habitId,
                        date = state.date,
                        status = state.status,
                        comment = state.comment.trim().takeIf { it.isNotEmpty() }
                    )
                    checkInRepository.logCheckIn(checkIn)
                }

                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to save check-in")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
