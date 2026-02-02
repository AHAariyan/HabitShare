package com.habit.habitshare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habit.habitshare.data.local.entity.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckInEntity): Long

    @Update
    suspend fun update(checkIn: CheckInEntity)

    @Delete
    suspend fun delete(checkIn: CheckInEntity)

    @Query("DELETE FROM check_ins WHERE habitId = :habitId AND date = :date")
    suspend fun deleteByHabitAndDate(habitId: Long, date: Long)

    @Query("SELECT * FROM check_ins WHERE id = :id")
    suspend fun getById(id: Long): CheckInEntity?

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND date = :date")
    suspend fun getByHabitAndDate(habitId: Long, date: Long): CheckInEntity?

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND date = :date")
    fun getByHabitAndDateFlow(habitId: Long, date: Long): Flow<CheckInEntity?>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId ORDER BY date DESC")
    fun getCheckInsForHabit(habitId: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getCheckInsForHabitInRange(habitId: Long, startDate: Long, endDate: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getCheckInsForHabitInRangeSync(habitId: Long, startDate: Long, endDate: Long): List<CheckInEntity>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND status = 'SUCCESS' ORDER BY date DESC")
    suspend fun getSuccessfulCheckIns(habitId: Long): List<CheckInEntity>

    @Query("SELECT COUNT(*) FROM check_ins WHERE habitId = :habitId AND status = 'SUCCESS'")
    suspend fun getSuccessCount(habitId: Long): Int

    @Query("SELECT COUNT(*) FROM check_ins WHERE habitId = :habitId AND status != 'SKIP'")
    suspend fun getTotalCountExcludingSkips(habitId: Long): Int
}
