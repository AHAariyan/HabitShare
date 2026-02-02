package com.habit.habitshare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habit.habitshare.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteById(reminderId: Long)

    @Query("DELETE FROM reminders WHERE habitId = :habitId")
    suspend fun deleteByHabitId(habitId: Long)

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getById(reminderId: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE habitId = :habitId")
    fun getRemindersForHabit(habitId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE habitId = :habitId")
    suspend fun getRemindersForHabitSync(habitId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    suspend fun getEnabledReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<ReminderEntity>>
}
