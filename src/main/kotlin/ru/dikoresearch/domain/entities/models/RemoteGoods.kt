package ru.dikoresearch.domain.entities.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteGoods(
    val art: String,
    val name: String,
    val count: String,
    val price: Int
)
