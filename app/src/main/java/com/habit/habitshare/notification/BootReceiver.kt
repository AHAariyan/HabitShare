package com.habit.habitshare.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.habit.habitshare.domain.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    @Inject
    lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        Log.d(TAG, "Boot completed, rescheduling all reminders")

        val pendingResult = goAsync()

        scope.launch {
            try {
                // Get all habits and their reminders
                val habits = habitRepository.getAllHabits().first()

                habits.forEach { habit ->
                    val reminders = habitRepository.getRemindersForHabit(habit.id).first()

                    reminders.filter { it.isEnabled }.forEach { reminder ->
                        reminderScheduler.scheduleReminder(reminder, habit.title)
                    }
                }

                Log.d(TAG, "All reminders rescheduled after boot")
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling reminders: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
