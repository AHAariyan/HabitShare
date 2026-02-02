package com.habit.habitshare.data.mapper

import com.habit.habitshare.data.local.entity.CheckInEntity
import com.habit.habitshare.domain.model.CheckIn
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun CheckInEntity.toDomain(): CheckIn {
    return CheckIn(
        id = id,
        habitId = habitId,
        date = LocalDate.ofEpochDay(date),
        status = status,
        comment = comment,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault())
    )
}

fun CheckIn.toEntity(): CheckInEntity {
    return CheckInEntity(
        id = id,
        habitId = habitId,
        date = date.toEpochDay(),
        status = status,
        comment = comment,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
