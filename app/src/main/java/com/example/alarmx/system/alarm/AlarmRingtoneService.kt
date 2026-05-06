package com.example.alarmx.system.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alarmx.MainActivity
import com.example.alarmx.R

/**
 * Foreground service that plays the alarm ringtone.
 */
class AlarmRingtoneService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentAlarmId: Long = -1L
    private var currentSoundUri: String? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        ensureChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            AlarmIntents.ACTION_STOP_RINGING -> {
                stopRingingAndSelf()
                return START_NOT_STICKY
            }
        }

        val alarmId = intent?.getLongExtra(AlarmIntents.EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }
        currentAlarmId = alarmId
        currentSoundUri = intent?.getStringExtra(AlarmIntents.EXTRA_ALARM_SOUND)

        startInForeground(alarmId)
        acquireWakeLock()
        startRingtone()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopRingtone()
        releaseWakeLock()
        super.onDestroy()
    }

    private fun startInForeground(alarmId: Long) {
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            action = AlarmIntents.ACTION_SHOW_DISMISS
            putExtra(AlarmIntents.EXTRA_ALARM_ID, alarmId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val fullScreenPi = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            fullScreenIntent,
            pendingIntentFlags()
        )

        val notification = NotificationCompat.Builder(this, AlarmIntents.CHANNEL_ID_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Alarm firing — tap to dismiss")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPi, true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                AlarmIntents.NOTIFICATION_ID_ALARM,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(AlarmIntents.NOTIFICATION_ID_ALARM, notification)
        }
    }

    private fun startRingtone() {
        val uri: Uri = resolveSoundUri(currentSoundUri)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: return
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmRingtoneService, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmRingtoneService", "Failed to play ringtone", e)
        }
    }

    private fun resolveSoundUri(raw: String?): Uri? {
        if (raw.isNullOrBlank() || raw == "default") return null
        return try { Uri.parse(raw) } catch (e: Exception) { null }
    }

    private fun stopRingtone() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmX:Service").apply {
            acquire(10 * 60 * 1000L) // 10 min cap
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }

    private fun stopRingingAndSelf() {
        stopRingtone()
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
    }

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
