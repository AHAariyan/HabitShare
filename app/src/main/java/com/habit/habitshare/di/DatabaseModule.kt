package com.habit.habitshare.di

import android.content.Context
import androidx.room.Room
import com.habit.habitshare.data.local.HabitShareDatabase
import com.habit.habitshare.data.local.dao.CheckInDao
import com.habit.habitshare.data.local.dao.HabitDao
import com.habit.habitshare.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitShareDatabase {
        return Room.databaseBuilder(
            context,
            HabitShareDatabase::class.java,
            HabitShareDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitShareDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideCheckInDao(database: HabitShareDatabase): CheckInDao {
        return database.checkInDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: HabitShareDatabase): ReminderDao {
        return database.reminderDao()
    }
}
