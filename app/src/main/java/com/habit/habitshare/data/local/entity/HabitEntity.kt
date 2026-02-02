package com.habit.habitshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habit.habitshare.domain.model.FrequencyType

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val frequencyType: FrequencyType,
    val frequencyValue: Int?,           // For PER_WEEK (1-7) or PER_MONTH (1-31)
    val specificDays: String?,          // JSON: "[1,3,5]" for Mon, Wed, Fri (DayOfWeek ordinals)
    val isPrivate: Boolean,
    val createdAt: Long,                // Epoch millis
    val updatedAt: Long                 // Epoch millis
)
