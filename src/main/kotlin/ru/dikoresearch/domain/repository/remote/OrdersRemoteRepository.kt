package ru.dikoresearch.domain.repository.remote

import ru.dikoresearch.domain.entities.models.RemoteOrder

interface OrdersRemoteRepository {
    suspend fun getOrderByOrderName(orderName: String): RemoteOrder?
}