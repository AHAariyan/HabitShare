package com.habit.habitshare.domain.repository

import com.habit.habitshare.domain.model.Habit
import com.habit.habitshare.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    fun getAllHabits(): Flow<List<Habit>>

    fun getHabitById(habitId: Long): Flow<Habit?>

    suspend fun getHabitByIdSync(habitId: Long): Habit?

    suspend fun createHabit(habit: Habit, reminders: List<Reminder>): Long

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habitId: Long)

    fun getRemindersForHabit(habitId: Long): Flow<List<Reminder>>

    suspend fun addReminder(reminder: Reminder): Long

    suspend fun updateReminder(reminder: Reminder)

    suspend fun deleteReminder(reminderId: Long)
}
