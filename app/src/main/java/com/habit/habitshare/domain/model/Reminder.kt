package com.habit.habitshare.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Reminder(
    val id: Long = 0,
    val habitId: Long,
    val time: LocalTime,
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),  // Default: all days
    val isEnabled: Boolean = true
)
