package com.habit.habitshare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habit.habitshare.data.local.converter.Converters
import com.habit.habitshare.data.local.dao.CheckInDao
import com.habit.habitshare.data.local.dao.HabitDao
import com.habit.habitshare.data.local.dao.ReminderDao
import com.habit.habitshare.data.local.entity.CheckInEntity
import com.habit.habitshare.data.local.entity.HabitEntity
import com.habit.habitshare.data.local.entity.ReminderEntity

@Database(
    entities = [
        HabitEntity::class,
        CheckInEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitShareDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun checkInDao(): CheckInDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "habitshare_db"
    }
}
