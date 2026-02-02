package com.habit.habitshare.presentation.navigation

sealed class Screen(val route: String) {

    object Home : Screen("home")

    object CreateHabit : Screen("create_habit")

    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }

    object CheckIn : Screen("check_in/{habitId}/{date}") {
        fun createRoute(habitId: Long, date: String) = "check_in/$habitId/$date"
    }

    object Calendar : Screen("calendar/{habitId}") {
        fun createRoute(habitId: Long) = "calendar/$habitId"
    }

    // Frequency selection screen for creating/editing habits
    object Frequency : Screen("frequency/{frequencyType}/{frequencyValue}/{specificDays}") {
        fun createRoute(
            frequencyType: String,
            frequencyValue: Int,
            specificDays: String
        ) = "frequency/$frequencyType/$frequencyValue/$specificDays"
    }

    // Reminders screen for creating/editing habits
    object Reminders : Screen("reminders/{habitId}") {
        fun createRoute(habitId: Long) = "reminders/$habitId"
    }
}
