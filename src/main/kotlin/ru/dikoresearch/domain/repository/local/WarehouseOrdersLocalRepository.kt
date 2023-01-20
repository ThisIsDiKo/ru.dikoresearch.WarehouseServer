package ru.dikoresearch.domain.repository.local

import ru.dikoresearch.domain.entities.models.Order


interface WarehouseOrdersLocalRepository {
    suspend fun getAllOrders(): List<Order>

    suspend fun getOrderByOrderName(orderName: String): Order?

    suspend fun insertNewOrder(order: Order): Order?
}