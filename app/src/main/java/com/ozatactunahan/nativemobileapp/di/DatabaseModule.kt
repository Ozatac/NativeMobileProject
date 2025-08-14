package com.ozatactunahan.nativemobileapp.di

import android.content.Context
import com.ozatactunahan.nativemobileapp.data.local.AppDatabase
import com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao
import com.ozatactunahan.nativemobileapp.data.repository.FavoriteRepositoryImpl
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository {
        return FavoriteRepositoryImpl(favoriteDao)
    }
}
