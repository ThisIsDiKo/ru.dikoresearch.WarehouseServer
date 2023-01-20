package ru.dikoresearch.domain.entities.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String
)
