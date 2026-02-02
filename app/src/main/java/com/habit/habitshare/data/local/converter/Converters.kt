package com.habit.habitshare.data.local.converter

import androidx.room.TypeConverter
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.domain.model.FrequencyType

class Converters {

    @TypeConverter
    fun fromFrequencyType(value: FrequencyType): String = value.name

    @TypeConverter
    fun toFrequencyType(value: String): FrequencyType = FrequencyType.valueOf(value)

    @TypeConverter
    fun fromCheckInStatus(value: CheckInStatus): String = value.name

    @TypeConverter
    fun toCheckInStatus(value: String): CheckInStatus = CheckInStatus.valueOf(value)
}
