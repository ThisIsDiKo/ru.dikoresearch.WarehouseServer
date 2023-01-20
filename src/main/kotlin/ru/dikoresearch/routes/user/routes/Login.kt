package ru.dikoresearch.routes.user.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.entities.requests.LoginRequest
import ru.dikoresearch.domain.entities.responses.LoginResponse
import ru.dikoresearch.domain.repository.local.WarehouseUsersLocalRepository
import ru.dikoresearch.domain.security.hashing.HashingService
import ru.dikoresearch.domain.security.hashing.SaltedHash
import ru.dikoresearch.domain.security.token.TokenClaim
import ru.dikoresearch.domain.security.token.TokenConfig
import ru.dikoresearch.domain.security.token.TokenService

fun Route.login(
    usersLocalRepository: WarehouseUsersLocalRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("login"){
        val loginRequest = kotlin.runCatching { call.receiveNullable<LoginRequest>() }.getOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect Login Object")

        if (loginRequest.username.isBlank()) return@post call.respond(HttpStatusCode.NotFound, "Empty username")
        if (loginRequest.password.isBlank()) return@post call.respond(HttpStatusCode.NotFound, "Empty password")

        val user = usersLocalRepository.getUserByUserName(loginRequest.username)
            ?: return@post call.respond(HttpStatusCode.NotFound, "Incorrect username or password")

        val isValidPassword = hashingService.verify(
            value = loginRequest.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            return@post call.respond(HttpStatusCode.NotFound, "Incorrect username or password")
        }

        val token = tokenService.generate(
            tokenConfig = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            ),
            TokenClaim(
                name = "username",
                value = user.username
            )
        )

        call.respond(HttpStatusCode.OK, LoginResponse(username = loginRequest.username, token = token))
    }
}