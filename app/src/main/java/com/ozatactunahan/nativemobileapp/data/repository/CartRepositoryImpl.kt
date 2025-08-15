package com.ozatactunahan.nativemobileapp.data.repository

import com.ozatactunahan.nativemobileapp.data.local.dao.CartDao
import com.ozatactunahan.nativemobileapp.data.local.dao.OrderDao
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) : CartRepository {

    override fun getAllCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return cartDao.getCartItemById(productId)
    }

    override suspend fun addToCart(product: com.ozatactunahan.nativemobileapp.data.model.Product) {
        val cartItem = CartItem(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            productName = product.name,
            productImage = product.image,
            productPrice = product.price,
            productBrand = product.brand,
            quantity = 1
        )
        cartDao.insertCartItem(cartItem)
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val cartItem = cartDao.getCartItemById(productId)
        cartItem?.let {
            val updatedItem = it.copy(quantity = quantity)
            cartDao.updateCartItem(updatedItem)
        }
    }

    override suspend fun removeFromCart(productId: String) {
        cartDao.deleteCartItemById(productId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }

    override fun getTotalAmount(): Flow<Double?> {
        return cartDao.getTotalAmount()
    }

    override suspend fun placeOrder(): String {
        val cartItems = cartDao.getAllCartItems().first()
        val totalAmount = cartDao.getTotalAmount().first() ?: 0.0
        
        val orderNumber = "ORD-${System.currentTimeMillis()}"
        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNumber,
            items = cartItems,
            totalAmount = totalAmount
        )
        
        orderDao.insertOrder(order)
        cartDao.clearCart()
        
        return orderNumber
    }
}
