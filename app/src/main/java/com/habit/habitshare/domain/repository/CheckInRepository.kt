package com.habit.habitshare.domain.repository

import com.habit.habitshare.domain.model.CheckIn
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CheckInRepository {

    fun getCheckInsForHabit(habitId: Long): Flow<List<CheckIn>>

    fun getCheckInsForHabitInRange(habitId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<CheckIn>>

    suspend fun getCheckInsForHabitInRangeSync(habitId: Long, startDate: LocalDate, endDate: LocalDate): List<CheckIn>

    fun getCheckInForDate(habitId: Long, date: LocalDate): Flow<CheckIn?>

    suspend fun getCheckInForDateSync(habitId: Long, date: LocalDate): CheckIn?

    suspend fun logCheckIn(checkIn: CheckIn): Long

    suspend fun deleteCheckIn(habitId: Long, date: LocalDate)

    suspend fun calculateStreak(habitId: Long): Int

    suspend fun calculateOverallPercentage(habitId: Long): Float
}
