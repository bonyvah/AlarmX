package com.example.alarmx.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.alarmx.domain.model.DifficultyLevel
import com.example.alarmx.domain.model.ThemeMode
import com.example.alarmx.domain.model.UserPreferences
import com.example.alarmx.domain.repository.PreferencesRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Preferences DataStore-backed implementation of [PreferencesRepository].
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    private val defaults: UserPreferences = UserPreferences()

    override val preferences: Flow<UserPreferences> = dataStore.data
        .catch { cause ->
            if (cause is IOException) emit(emptyPreferences()) else throw cause
        }
        .map { prefs -> prefs.toDomain(defaults) }

    override suspend fun get(): UserPreferences = preferences.first()

    override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        dataStore.edit { mutable ->
            val current = mutable.toDomain(defaults)
            val updated = transform(current)
            mutable.applyDomain(updated)
        }
    }
}

private fun Preferences.toDomain(defaults: UserPreferences): UserPreferences =
    UserPreferences(
        difficulty = this[PreferencesKeys.DIFFICULTY]
            ?.let(::parseDifficulty)
            ?: defaults.difficulty,
        snoozeEnabled = this[PreferencesKeys.SNOOZE_ENABLED]
            ?: defaults.snoozeEnabled,
        defaultSnoozeMinutes = this[PreferencesKeys.DEFAULT_SNOOZE_MINUTES]
            ?: defaults.defaultSnoozeMinutes,
        sound = this[PreferencesKeys.SOUND]
            ?: defaults.sound,
        themeMode = this[PreferencesKeys.THEME_MODE]
            ?.let(::parseThemeMode)
            ?: defaults.themeMode,
        maxSnoozeCount = this[PreferencesKeys.MAX_SNOOZE_COUNT]
            ?: defaults.maxSnoozeCount,
        hapticsOnWrongAnswer = this[PreferencesKeys.HAPTICS_ON_WRONG_ANSWER]
            ?: defaults.hapticsOnWrongAnswer,
    )

private fun androidx.datastore.preferences.core.MutablePreferences.applyDomain(
    prefs: UserPreferences,
) {
    this[PreferencesKeys.DIFFICULTY] = prefs.difficulty.name
    this[PreferencesKeys.SNOOZE_ENABLED] = prefs.snoozeEnabled
    this[PreferencesKeys.DEFAULT_SNOOZE_MINUTES] = prefs.defaultSnoozeMinutes
    this[PreferencesKeys.SOUND] = prefs.sound
    this[PreferencesKeys.THEME_MODE] = prefs.themeMode.name
    this[PreferencesKeys.MAX_SNOOZE_COUNT] = prefs.maxSnoozeCount
    this[PreferencesKeys.HAPTICS_ON_WRONG_ANSWER] = prefs.hapticsOnWrongAnswer
}

private fun parseDifficulty(raw: String): DifficultyLevel =
    runCatching { DifficultyLevel.valueOf(raw) }
        .getOrDefault(DifficultyLevel.MEDIUM)

private fun parseThemeMode(raw: String): ThemeMode =
    runCatching { ThemeMode.valueOf(raw) }
        .getOrDefault(ThemeMode.SYSTEM)
