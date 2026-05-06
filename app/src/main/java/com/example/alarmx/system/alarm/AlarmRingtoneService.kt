package com.example.alarmx.system.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

class AlarmRingtoneService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = context.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(
                    AlarmIntents.CHANNEL_ID_ALARM,
                    "Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setSound(null, null)
                }
                nm.createNotificationChannel(channel)
            }
        }
    }
}
