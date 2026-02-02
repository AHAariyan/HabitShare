package com.habit.habitshare.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class CheckIn(
    val id: Long = 0,
    val habitId: Long,
    val date: LocalDate,
    val status: CheckInStatus,
    val comment: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
