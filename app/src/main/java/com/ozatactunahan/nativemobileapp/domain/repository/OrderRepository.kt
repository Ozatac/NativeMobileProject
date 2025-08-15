package com.ozatactunahan.nativemobileapp.domain.repository

import com.ozatactunahan.nativemobileapp.data.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<Order>>
    suspend fun getOrderById(orderId: String): Order?
    suspend fun insertOrder(order: Order)
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(orderId: String)
    fun getOrderCount(): Flow<Int>
}
