package com.habit.habitshare.data.mapper

import com.habit.habitshare.data.local.entity.HabitEntity
import com.habit.habitshare.domain.model.Habit
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        frequencyType = frequencyType,
        frequencyValue = frequencyValue,
        specificDays = specificDays?.let { json ->
            json.removeSurrounding("[", "]")
                .split(",")
                .filter { it.isNotBlank() }
                .map { DayOfWeek.of(it.trim().toInt()) }
                .toSet()
        } ?: emptySet(),
        isPrivate = isPrivate,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault())
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        title = title,
        description = description,
        frequencyType = frequencyType,
        frequencyValue = frequencyValue,
        specificDays = if (specificDays.isEmpty()) null else "[${specificDays.joinToString(",") { it.value.toString() }}]",
        isPrivate = isPrivate,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
