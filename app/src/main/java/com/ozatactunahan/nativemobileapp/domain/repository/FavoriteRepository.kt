package com.ozatactunahan.nativemobileapp.domain.repository

import com.ozatactunahan.nativemobileapp.data.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun addToFavorites(product: Product)
    suspend fun removeFromFavorites(productId: String)
    fun getFavoriteCount(): Flow<Int>
}
