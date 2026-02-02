package com.habit.habitshare.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.habit.habitshare.domain.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ReminderScheduler"
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_TITLE = "extra_habit_title"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(reminder: Reminder, habitTitle: String) {
        if (!reminder.isEnabled) {
            Log.d(TAG, "Reminder ${reminder.id} is disabled, skipping")
            return
        }

        // Schedule for each day in the reminder's days set
        reminder.days.forEach { dayOfWeek ->
            scheduleForDay(reminder, habitTitle, dayOfWeek)
        }

        Log.d(TAG, "Scheduled reminder ${reminder.id} for habit '$habitTitle' on days: ${reminder.days}")
    }

    private fun scheduleForDay(reminder: Reminder, habitTitle: String, dayOfWeek: DayOfWeek) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_HABIT_ID, reminder.habitId)
            putExtra(EXTRA_HABIT_TITLE, habitTitle)
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        // Create unique request code for each reminder + day combination
        val requestCode = generateRequestCode(reminder.id, dayOfWeek)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate next trigger time for this day
        val triggerTime = calculateNextTriggerTime(reminder.time, dayOfWeek)

        // Schedule the alarm
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            Log.d(TAG, "Alarm scheduled for $dayOfWeek at ${reminder.time}, trigger: $triggerTime")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to schedule exact alarm: ${e.message}")
            // Fall back to inexact alarm
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun calculateNextTriggerTime(time: LocalTime, dayOfWeek: DayOfWeek): Long {
        val now = LocalDateTime.now()
        var targetDate = now.toLocalDate()

        // Find the next occurrence of this day of week
        if (targetDate.dayOfWeek == dayOfWeek) {
            // If it's today, check if the time has passed
            if (now.toLocalTime().isAfter(time)) {
                // Schedule for next week
                targetDate = targetDate.plusWeeks(1)
            }
        } else {
            // Find next occurrence of this day
            targetDate = targetDate.with(TemporalAdjusters.nextOrSame(dayOfWeek))
            if (targetDate == now.toLocalDate() && now.toLocalTime().isAfter(time)) {
                targetDate = targetDate.plusWeeks(1)
            }
        }

        val targetDateTime = LocalDateTime.of(targetDate, time)
        return targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun cancelReminder(reminder: Reminder) {
        reminder.days.forEach { dayOfWeek ->
            cancelForDay(reminder.id, reminder.habitId, dayOfWeek)
        }
        Log.d(TAG, "Cancelled reminder ${reminder.id}")
    }

    fun cancelAllRemindersForHabit(habitId: Long, reminders: List<Reminder>) {
        reminders.forEach { reminder ->
            cancelReminder(reminder)
        }
        Log.d(TAG, "Cancelled all reminders for habit $habitId")
    }

    private fun cancelForDay(reminderId: Long, habitId: Long, dayOfWeek: DayOfWeek) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val requestCode = generateRequestCode(reminderId, dayOfWeek)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun generateRequestCode(reminderId: Long, dayOfWeek: DayOfWeek): Int {
        // Create unique request code combining reminder ID and day
        return (reminderId * 10 + dayOfWeek.value).toInt()
    }

    fun rescheduleReminder(reminder: Reminder, habitTitle: String) {
        // Cancel existing and schedule new
        cancelReminder(reminder)
        scheduleReminder(reminder, habitTitle)
    }
}
