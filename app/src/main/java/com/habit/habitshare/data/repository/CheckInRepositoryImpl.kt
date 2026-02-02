package com.habit.habitshare.data.repository

import com.habit.habitshare.data.local.dao.CheckInDao
import com.habit.habitshare.data.mapper.toDomain
import com.habit.habitshare.data.mapper.toEntity
import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepositoryImpl @Inject constructor(
    private val checkInDao: CheckInDao
) : CheckInRepository {

    override fun getCheckInsForHabit(habitId: Long): Flow<List<CheckIn>> {
        return checkInDao.getCheckInsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCheckInsForHabitInRange(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CheckIn>> {
        return checkInDao.getCheckInsForHabitInRange(
            habitId,
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCheckInsForHabitInRangeSync(
        habitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<CheckIn> {
        return checkInDao.getCheckInsForHabitInRangeSync(
            habitId,
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { it.toDomain() }
    }

    override fun getCheckInForDate(habitId: Long, date: LocalDate): Flow<CheckIn?> {
        return checkInDao.getByHabitAndDateFlow(habitId, date.toEpochDay()).map { it?.toDomain() }
    }

    override suspend fun getCheckInForDateSync(habitId: Long, date: LocalDate): CheckIn? {
        return checkInDao.getByHabitAndDate(habitId, date.toEpochDay())?.toDomain()
    }

    override suspend fun logCheckIn(checkIn: CheckIn): Long {
        return checkInDao.insert(checkIn.toEntity())
    }

    override suspend fun deleteCheckIn(habitId: Long, date: LocalDate) {
        checkInDao.deleteByHabitAndDate(habitId, date.toEpochDay())
    }

    override suspend fun calculateStreak(habitId: Long): Int {
        val checkIns = checkInDao.getSuccessfulCheckIns(habitId)
        if (checkIns.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()

        // Check if today has a success, otherwise start from yesterday
        val todayCheckIn = checkInDao.getByHabitAndDate(habitId, currentDate.toEpochDay())
        if (todayCheckIn?.status != CheckInStatus.SUCCESS) {
            currentDate = currentDate.minusDays(1)
        }

        // Count consecutive success days going backwards
        while (true) {
            val checkIn = checkInDao.getByHabitAndDate(habitId, currentDate.toEpochDay())
            when (checkIn?.status) {
                CheckInStatus.SUCCESS -> {
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
                CheckInStatus.SKIP -> {
                    // Skip doesn't break streak, just skip the day
                    currentDate = currentDate.minusDays(1)
                }
                else -> break // FAIL or no check-in breaks the streak
            }
        }

        return streak
    }

    override suspend fun calculateOverallPercentage(habitId: Long): Float {
        val successCount = checkInDao.getSuccessCount(habitId)
        val totalCount = checkInDao.getTotalCountExcludingSkips(habitId)

        return if (totalCount == 0) {
            0f
        } else {
            (successCount.toFloat() / totalCount.toFloat()) * 100f
        }
    }
}
