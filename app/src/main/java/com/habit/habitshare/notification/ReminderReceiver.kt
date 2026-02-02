package com.habit.habitshare.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.habit.habitshare.domain.repository.CheckInRepository
import com.habit.habitshare.domain.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
    }

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var checkInRepository: CheckInRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(ReminderScheduler.EXTRA_HABIT_ID, -1)
        val habitTitle = intent.getStringExtra(ReminderScheduler.EXTRA_HABIT_TITLE) ?: "Your Habit"
        val reminderId = intent.getLongExtra(ReminderScheduler.EXTRA_REMINDER_ID, -1)

        Log.d(TAG, "Received reminder for habit $habitId: $habitTitle")

        if (habitId == -1L) {
            Log.e(TAG, "Invalid habit ID")
            return
        }

        // Use goAsync() to allow async work in BroadcastReceiver
        val pendingResult = goAsync()

        scope.launch {
            try {
                // Get current streak for the notification message
                val streak = checkInRepository.calculateStreak(habitId)

                // Show notification
                notificationHelper.showReminderNotification(
                    habitId = habitId,
                    habitTitle = habitTitle,
                    streak = streak
                )

                // Reschedule for next week (repeating alarm)
                val reminders = habitRepository.getRemindersForHabit(habitId).first()
                val reminder = reminders.find { it.id == reminderId }

                if (reminder != null && reminder.isEnabled) {
                    reminderScheduler.rescheduleReminder(reminder, habitTitle)
                }

                Log.d(TAG, "Notification shown and reminder rescheduled")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing reminder: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
