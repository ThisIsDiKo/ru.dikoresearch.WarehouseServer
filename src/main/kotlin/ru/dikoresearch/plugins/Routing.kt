package ru.dikoresearch.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.data.repository.local.WarehouseLocalRepositoryImpl
import ru.dikoresearch.domain.repository.local.ImageFileManager
import ru.dikoresearch.domain.repository.remote.OrdersRemoteRepository
import ru.dikoresearch.domain.security.hashing.HashingService
import ru.dikoresearch.domain.security.token.TokenConfig
import ru.dikoresearch.domain.security.token.TokenService
import ru.dikoresearch.routes.orders.orders
import ru.dikoresearch.routes.user.users

fun  Application.configureRouting(
    warehouseLocalRepository: WarehouseLocalRepositoryImpl,
    ordersRemoteRepository: OrdersRemoteRepository,
    imageFileManager: ImageFileManager,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
//    install(AutoHeadResponse)
    routing {
        get("/"){
            call.respondText("Hello world")
        }

        users(
            usersLocalRepository = warehouseLocalRepository,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )

        orders(
            warehouseImagesLocalRepository = warehouseLocalRepository,
            ordersRemoteRepository = ordersRemoteRepository,
            imageFileManager = imageFileManager
        )
    }
}