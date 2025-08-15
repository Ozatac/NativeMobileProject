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
    fun provideFavoriteRepository(): FavoriteRepository {
        // TODO: Implement FavoriteRepository
        return object : FavoriteRepository {
            override fun getAllFavorites(): kotlinx.coroutines.flow.Flow<List<com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity>> {
                return kotlinx.coroutines.flow.flowOf(emptyList())
            }
            
            override suspend fun addToFavorites(product: com.ozatactunahan.nativemobileapp.data.model.Product) {
                // TODO: Implement
            }
            
            override suspend fun removeFromFavorites(productId: String) {
                // TODO: Implement
            }
            
            override fun isFavorite(productId: String): kotlinx.coroutines.flow.Flow<Boolean> {
                return kotlinx.coroutines.flow.flowOf(false)
            }
            
            override fun getFavoriteCount(): kotlinx.coroutines.flow.Flow<Int> {
                return kotlinx.coroutines.flow.flowOf(0)
            }
        }
    }
}
