package com.habit.habitshare.domain.model

enum class FrequencyType {
    DAILY,          // Habit repeats every day
    SPECIFIC_DAYS,  // Habit on selected days of week (e.g., Mon, Wed, Fri)
    PER_WEEK,       // X times per week without fixed days
    PER_MONTH       // X times per month
}
