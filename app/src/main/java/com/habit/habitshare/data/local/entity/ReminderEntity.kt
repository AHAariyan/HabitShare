package com.habit.habitshare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"])]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val timeHour: Int,                  // 0-23
    val timeMinute: Int,                // 0-59
    val days: String,                   // JSON: "[0,1,2,3,4,5,6]" for DayOfWeek ordinals
    val isEnabled: Boolean
)
