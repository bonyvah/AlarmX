package com.example.alarmx.domain.repository

import com.example.alarmx.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    val preferences: Flow<UserPreferences>

    suspend fun get(): UserPreferences

    suspend fun update(transform: (UserPreferences) -> UserPreferences)
}