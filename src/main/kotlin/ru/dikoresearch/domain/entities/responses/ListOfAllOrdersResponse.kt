package ru.dikoresearch.domain.entities.responses

import kotlinx.serialization.Serializable
import ru.dikoresearch.domain.entities.models.Order

@Serializable
data class ListOfAllOrdersResponse(
    val orders: List<Order>
)
