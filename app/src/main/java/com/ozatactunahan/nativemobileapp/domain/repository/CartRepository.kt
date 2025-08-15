package com.ozatactunahan.nativemobileapp.domain.repository

import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.data.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getAllCartItems(): Flow<List<CartItem>>
    suspend fun getCartItemById(productId: String): CartItem?
    suspend fun addToCart(product: Product)
    suspend fun updateQuantity(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()
    fun getCartItemCount(): Flow<Int>
    fun getTotalAmount(): Flow<Double?>
    suspend fun placeOrder(): String
}
