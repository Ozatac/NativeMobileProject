package com.ozatactunahan.nativemobileapp.data.repository

import com.ozatactunahan.nativemobileapp.data.local.dao.OrderDao
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return orderDao.getOrderById(orderId)
    }

    override suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    override suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    override suspend fun deleteOrder(orderId: String) {
        val order = orderDao.getOrderById(orderId)
        order?.let {
            orderDao.deleteOrder(it)
        }
    }

    override fun getOrderCount(): Flow<Int> {
        return orderDao.getOrderCount()
    }
}
