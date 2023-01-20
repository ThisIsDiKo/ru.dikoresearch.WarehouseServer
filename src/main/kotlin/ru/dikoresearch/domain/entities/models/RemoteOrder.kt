package ru.dikoresearch.domain.entities.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteOrder(
    val date: String,
    @SerialName("num") val name: String,
    val uuid: String,
    val goods: List<RemoteGoods>
)
