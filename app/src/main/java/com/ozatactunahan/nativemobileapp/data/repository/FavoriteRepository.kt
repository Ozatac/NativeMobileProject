package com.ozatactunahan.nativemobileapp.data.repository

import com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }
    
    fun isFavorite(productId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }
    
    suspend fun addToFavorites(product: Product) {
        val favoriteEntity = FavoriteEntity(
            productId = product.id,
            name = product.name,
            brand = product.brand,
            price = product.price,
            imageUrl = product.image
        )
        favoriteDao.addToFavorites(favoriteEntity)
    }
    
    suspend fun removeFromFavorites(productId: String) {
        favoriteDao.removeFromFavoritesById(productId)
    }
    
    fun getFavoriteCount(): Flow<Int> {
        return favoriteDao.getFavoriteCount()
    }
}
