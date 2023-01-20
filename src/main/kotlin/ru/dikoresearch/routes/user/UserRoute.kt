package ru.dikoresearch.routes.user

import io.ktor.server.routing.*
import ru.dikoresearch.domain.repository.local.WarehouseUsersLocalRepository
import ru.dikoresearch.domain.security.hashing.HashingService
import ru.dikoresearch.domain.security.token.TokenConfig
import ru.dikoresearch.domain.security.token.TokenService
import ru.dikoresearch.routes.user.routes.login
import ru.dikoresearch.routes.user.routes.signup

fun Route.users(
    usersLocalRepository: WarehouseUsersLocalRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    route("user"){

        signup(
            usersLocalRepository = usersLocalRepository,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )

        login(
            usersLocalRepository = usersLocalRepository,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
    }
}