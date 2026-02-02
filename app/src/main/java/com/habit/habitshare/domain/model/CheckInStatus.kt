package com.habit.habitshare.domain.model

enum class CheckInStatus {
    SUCCESS,    // Green - Habit completed
    FAIL,       // Red - Habit missed
    SKIP        // Grey - Day excused (doesn't affect overall percentage)
}
