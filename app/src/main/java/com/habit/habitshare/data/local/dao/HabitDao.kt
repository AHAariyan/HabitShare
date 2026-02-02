package com.habit.habitshare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habit.habitshare.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteById(habitId: Long)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getById(habitId: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getByIdFlow(habitId: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isPrivate = 0 ORDER BY createdAt DESC")
    fun getSharedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun getHabitCount(): Int
}
