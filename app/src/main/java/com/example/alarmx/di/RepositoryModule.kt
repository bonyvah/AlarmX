package com.example.alarmx.di

import com.example.alarmx.data.prefs.PreferencesRepositoryImpl
import com.example.alarmx.data.repository.DefaultAlarmRepository
import com.example.alarmx.domain.challenge.ArithmeticTaskGenerator
import com.example.alarmx.domain.challenge.ChallengeProvider
import com.example.alarmx.domain.repository.AlarmRepository
import com.example.alarmx.domain.repository.PreferencesRepository
import com.example.alarmx.domain.ringing.RingingController
import com.example.alarmx.domain.scheduler.AlarmScheduler
import com.example.alarmx.system.alarm.AlarmManagerScheduler
import com.example.alarmx.system.alarm.AndroidRingingController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(impl: DefaultAlarmRepository): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmManagerScheduler): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindRingingController(impl: AndroidRingingController): RingingController

    @Binds
    @Singleton
    abstract fun bindChallengeProvider(impl: ArithmeticTaskGenerator): ChallengeProvider
}
