package com.habit.habitshare.di

import com.habit.habitshare.data.repository.CheckInRepositoryImpl
import com.habit.habitshare.data.repository.HabitRepositoryImpl
import com.habit.habitshare.domain.repository.CheckInRepository
import com.habit.habitshare.domain.repository.HabitRepository
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
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindCheckInRepository(impl: CheckInRepositoryImpl): CheckInRepository
}
