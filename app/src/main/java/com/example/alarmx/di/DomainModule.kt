package com.example.alarmx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideRandom(): Random = Random.Default
}
