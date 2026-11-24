package com.dabskutz.kalkulator.di

import android.content.Context
import com.dabskutz.kalkulator.data.history.HistoryRepository
import com.dabskutz.kalkulator.data.preferences.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDependencyModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(dataStoreManager: DataStoreManager): HistoryRepository {
        return HistoryRepository(dataStoreManager)
    }
}
