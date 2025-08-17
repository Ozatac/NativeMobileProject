package com.ozatactunahan.nativemobileapp.di

import android.content.Context
import com.ozatactunahan.nativemobileapp.data.local.AppDatabase
import com.ozatactunahan.nativemobileapp.data.local.dao.CartDao
import com.ozatactunahan.nativemobileapp.data.local.dao.OrderDao
import com.ozatactunahan.nativemobileapp.data.repository.CartRepositoryImpl
import com.ozatactunahan.nativemobileapp.data.repository.OrderRepositoryImpl
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
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
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }
    
    @Provides
    @Singleton
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao {
        return database.favoriteDao()
    }
    
    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao,
        orderDao: OrderDao
    ): com.ozatactunahan.nativemobileapp.domain.repository.CartRepository {
        return CartRepositoryImpl(cartDao, orderDao)
    }
    
    @Provides
    @Singleton
    fun provideOrderRepository(
        orderDao: OrderDao
    ): com.ozatactunahan.nativemobileapp.domain.repository.OrderRepository {
        return OrderRepositoryImpl(orderDao)
    }
    
    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteDao: com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao
    ): FavoriteRepository {
        return com.ozatactunahan.nativemobileapp.data.repository.FavoriteRepositoryImpl(favoriteDao)
    }
}
