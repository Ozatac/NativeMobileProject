package com.ozatactunahan.nativemobileapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val productId: String,
    val name: String,
    val brand: String,
    val price: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
