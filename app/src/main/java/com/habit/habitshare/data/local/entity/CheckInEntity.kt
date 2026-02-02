package com.habit.habitshare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habit.habitshare.domain.model.CheckInStatus

@Entity(
    tableName = "check_ins",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: Long,                     // Epoch day (LocalDate.toEpochDay)
    val status: CheckInStatus,
    val comment: String?,
    val createdAt: Long,                // Epoch millis
    val updatedAt: Long                 // Epoch millis
)
