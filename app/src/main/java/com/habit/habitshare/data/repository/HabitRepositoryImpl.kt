package com.habit.habitshare.data.repository

import com.habit.habitshare.data.local.dao.HabitDao
import com.habit.habitshare.data.local.dao.ReminderDao
import com.habit.habitshare.data.mapper.toDomain
import com.habit.habitshare.data.mapper.toEntity
import com.habit.habitshare.domain.model.Habit
import com.habit.habitshare.domain.model.Reminder
import com.habit.habitshare.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val reminderDao: ReminderDao
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHabitById(habitId: Long): Flow<Habit?> {
        return habitDao.getByIdFlow(habitId).map { it?.toDomain() }
    }

    override suspend fun getHabitByIdSync(habitId: Long): Habit? {
        return habitDao.getById(habitId)?.toDomain()
    }

    override suspend fun createHabit(habit: Habit, reminders: List<Reminder>): Long {
        val habitId = habitDao.insert(habit.toEntity())
        if (reminders.isNotEmpty()) {
            val reminderEntities = reminders.map { it.copy(habitId = habitId).toEntity() }
            reminderDao.insertAll(reminderEntities)
        }
        return habitId
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.update(habit.toEntity())
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteById(habitId)
    }

    override fun getRemindersForHabit(habitId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        return reminderDao.insert(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.update(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminderId: Long) {
        reminderDao.deleteById(reminderId)
    }
}
