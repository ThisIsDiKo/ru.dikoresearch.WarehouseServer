package ru.dikoresearch.domain.entities.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val username: String,
    val token: String
)
