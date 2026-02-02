package com.habit.habitshare.data.mapper

import com.habit.habitshare.data.local.entity.ReminderEntity
import com.habit.habitshare.domain.model.Reminder
import java.time.DayOfWeek
import java.time.LocalTime

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        habitId = habitId,
        time = LocalTime.of(timeHour, timeMinute),
        days = days.removeSurrounding("[", "]")
            .split(",")
            .filter { it.isNotBlank() }
            .map { DayOfWeek.of(it.trim().toInt()) }
            .toSet(),
        isEnabled = isEnabled
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        habitId = habitId,
        timeHour = time.hour,
        timeMinute = time.minute,
        days = "[${days.joinToString(",") { it.value.toString() }}]",
        isEnabled = isEnabled
    )
}
