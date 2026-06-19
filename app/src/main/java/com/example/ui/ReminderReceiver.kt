package com.example.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Restore alarms on reboot
            rescheduleFromPreferences(context)
        } else if (action == ACTION_SHOW_REMINDER) {
            // Show notification
            showNotification(context)
            // Reschedule same time tomorrow to keep the daily cycle active
            rescheduleFromPreferences(context)
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "quran_daily_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تذكير الورد اليومي (Al-Quran Reminder)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "قناة إشعارات تذكير قراءة الورد اليومي من القرآن الكريم"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Setup click action to open MainActivity
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Read preferences to see language and customize the notification message style
        val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "ar") ?: "ar"

        val title = when (lang) {
            "en" -> "Daily Quran Reading Reminder"
            "fr" -> "Rappel de lecture quotidienne"
            else -> "تذكير الورد اليومي للقرآن"
        }

        val text = when (lang) {
            "en" -> "It's time for your daily Quran portion. Keep up your spiritual routine! 📖"
            "fr" -> "C'est l'heure de votre lecture quotidienne du Coran. Conservez votre habitude spirituelle! 📖"
            else -> "حان موعد وردك اليومي من القرآن الكريم. حافظ على الاتصال بكتاب الله 📖"
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 2005
        const val ACTION_SHOW_REMINDER = "com.example.ACTION_SHOW_REMINDER"

        fun scheduleReminder(context: Context, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_SHOW_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // If set time is in the past, schedule it for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                // Exact alarm schedule requires extra permissions in newer Android APIs. Fallback to standard.
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        fun cancelReminder(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_SHOW_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }

        fun rescheduleFromPreferences(context: Context) {
            val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("reminder_enabled", false)
            if (isEnabled) {
                val hour = prefs.getInt("reminder_hour", 20)
                val minute = prefs.getInt("reminder_minute", 0)
                scheduleReminder(context, hour, minute)
            } else {
                cancelReminder(context)
            }
        }
    }
}
