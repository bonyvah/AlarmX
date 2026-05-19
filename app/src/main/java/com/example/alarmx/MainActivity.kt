package com.example.alarmx

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alarmx.domain.model.ThemeMode
import com.example.alarmx.domain.model.UserPreferences
import com.example.alarmx.domain.repository.PreferencesRepository
import com.example.alarmx.system.alarm.AlarmIntents
import com.example.alarmx.ui.nav.AlarmXNavGraph
import com.example.alarmx.ui.theme.AlarmXTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private var alarmTrigger by mutableStateOf<AlarmTrigger?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureLockScreen()
        enableEdgeToEdge()
        
        readAlarmIdFromIntent(intent)?.let { id ->
            alarmTrigger = AlarmTrigger(id)
        }

        setContent {
            val prefs by preferencesRepository.preferences
                .collectAsStateWithLifecycle(initialValue = UserPreferences())
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (prefs.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark
            }
            AlarmXTheme(darkTheme = darkTheme) {
                AlarmXNavGraph(alarmTrigger = alarmTrigger)
            }
        }

        maybeRequestNotificationPermission()
        maybeRequestAutostart()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readAlarmIdFromIntent(intent)?.let { id ->
            alarmTrigger = AlarmTrigger(id)
        }
    }

    private fun configureLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            )
        }
    }

    private fun readAlarmIdFromIntent(intent: Intent?): Long? {
        if (intent == null) return null
        if (intent.action != AlarmIntents.ACTION_SHOW_DISMISS) return null
        val id = intent.getLongExtra(AlarmIntents.EXTRA_ALARM_ID, -1L)
        return id.takeIf { it != -1L }
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * On Xiaomi (MIUI) and Huawei (EMUI/HarmonyOS) devices the OS will kill background
     * services unless the user explicitly enables "Autostart" for the app.
     * We detect the manufacturer and show an explanatory dialog exactly once —
     * after the user interacts with it the flag is persisted so it never shows again.
     * (There is no public API to query whether autostart is actually granted.)
     */
    private fun maybeRequestAutostart() {
        val prefs = getSharedPreferences("alarmx_prefs", MODE_PRIVATE)
        if (prefs.getBoolean(KEY_AUTOSTART_PROMPTED, false)) return

        val manufacturer = Build.MANUFACTURER.lowercase()
        val (title, message, intent) = when {
            manufacturer.contains("xiaomi") -> Triple(
                "Enable Autostart",
                "AlarmX needs Autostart permission to fire alarms reliably on your Xiaomi device.\n\nPlease enable it in the next screen.",
                Intent().apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
            )
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> Triple(
                "Enable Autostart",
                "AlarmX needs Autostart permission to fire alarms reliably on your Huawei device.\n\nPlease enable it in the next screen.",
                Intent().apply {
                    setClassName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                    )
                }
            )
            else -> return   // Not a targeted OEM — no action needed
        }

        // Only show the dialog if the target Activity actually exists on this device
        val canOpen = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null
        if (!canOpen) return

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Open Settings") { _, _ ->
                prefs.edit().putBoolean(KEY_AUTOSTART_PROMPTED, true).apply()
                runCatching { startActivity(intent) }
            }
            .setNegativeButton("Not Now") { _, _ ->
                prefs.edit().putBoolean(KEY_AUTOSTART_PROMPTED, true).apply()
            }
            .show()
    }

    companion object {
        private const val KEY_AUTOSTART_PROMPTED = "autostart_prompted"
    }
}

data class AlarmTrigger(val id: Long, val time: Long = System.currentTimeMillis())