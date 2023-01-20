package ru.dikoresearch.domain.entities.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val orderId: Int,
    val orderName: String,
    val userId: Int,
    val username: String,
    val status: String,
    val createdAt: String,
    val comment: String,
    val images: List<String>,
    val uuid: String,
    val goods: List<RemoteGoods>,
    val checked: Int
)