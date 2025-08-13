package com.ozatactunahan.nativemobileapp.data.local.dao

import androidx.room.*
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)")
    fun isFavorite(productId: String): Flow<Boolean>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)
    
    @Delete
    suspend fun removeFromFavorites(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun removeFromFavoritesById(productId: String)
    
    @Query("SELECT COUNT(*) FROM favorites")
    fun getFavoriteCount(): Flow<Int>
}
