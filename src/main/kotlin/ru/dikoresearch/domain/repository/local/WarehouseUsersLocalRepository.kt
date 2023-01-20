package ru.dikoresearch.domain.repository.local

import ru.dikoresearch.domain.entities.models.User

interface WarehouseUsersLocalRepository {
    suspend fun getUserByUserName(username: String): User?
    suspend fun insertNewUser(user: User): User?
}