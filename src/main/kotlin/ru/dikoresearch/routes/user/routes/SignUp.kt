package ru.dikoresearch.routes.user.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.entities.models.User
import ru.dikoresearch.domain.entities.requests.CreateUserRequest
import ru.dikoresearch.domain.entities.responses.LoginResponse
import ru.dikoresearch.domain.repository.local.WarehouseUsersLocalRepository
import ru.dikoresearch.domain.security.hashing.HashingService
import ru.dikoresearch.domain.security.token.TokenClaim
import ru.dikoresearch.domain.security.token.TokenConfig
import ru.dikoresearch.domain.security.token.TokenService

fun Route.signup(
    usersLocalRepository: WarehouseUsersLocalRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("signup"){
        val createUserRequest = kotlin.runCatching { call.receiveNullable<CreateUserRequest>() }.getOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect createuserrequest object")

        if(createUserRequest.username.isBlank() || createUserRequest.password.isBlank()){
            return@post call.respond(HttpStatusCode.BadRequest, "Field is blank")
        }
        if(createUserRequest.password.length < 8){
            return@post call.respond(HttpStatusCode.BadRequest, "Password is short")
        }
        if(createUserRequest.username.length < 6){
            return@post call.respond(HttpStatusCode.BadRequest, "Username is short")
        }

        val saltedHash = hashingService.generateSaltedHash(createUserRequest.password)

        val user = User(
            username = createUserRequest.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        val createdUser = usersLocalRepository.insertNewUser(user)
            ?: return@post call.respond(HttpStatusCode.BadRequest, "User already exists")

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

        call.respond(HttpStatusCode.OK, LoginResponse(username = createdUser.username, token = token))
    }
}