package com.ozatactunahan.nativemobileapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productPrice: String,
    val productBrand: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)
