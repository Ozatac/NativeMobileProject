package com.ozatactunahan.nativemobileapp.data.repository

import com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    
    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }
    
    override fun isFavorite(productId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }
    
    override suspend fun addToFavorites(product: Product) {
        val favoriteEntity = FavoriteEntity(
            productId = product.id,
            name = product.name,
            brand = product.brand,
            price = product.price,
            imageUrl = product.image
        )
        favoriteDao.addToFavorites(favoriteEntity)
    }
    
    override suspend fun removeFromFavorites(productId: String) {
        favoriteDao.removeFromFavoritesById(productId)
    }
    
    override fun getFavoriteCount(): Flow<Int> {
        return favoriteDao.getFavoriteCount()
    }
}
