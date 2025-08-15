package com.ozatactunahan.nativemobileapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey
    val productId: String,
    val name: String,
    val brand: String,
    val price: String,
    val imageUrl: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)
