package com.habit.habitshare.domain.model

import java.time.DayOfWeek
import java.time.LocalDateTime

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val frequencyType: FrequencyType,
    val frequencyValue: Int? = null,        // For PER_WEEK (1-7) or PER_MONTH (1-31)
    val specificDays: Set<DayOfWeek> = emptySet(),  // For SPECIFIC_DAYS type
    val isPrivate: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
