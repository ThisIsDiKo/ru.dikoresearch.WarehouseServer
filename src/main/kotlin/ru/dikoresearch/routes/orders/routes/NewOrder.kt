package ru.dikoresearch.routes.orders.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.entities.models.Order
import ru.dikoresearch.domain.repository.local.WarehouseOrdersLocalRepository

fun Route.newOrder(
    warehouseOrdersLocalRepository: WarehouseOrdersLocalRepository
){
    post("new"){
        val order = kotlin.runCatching { call.receiveNullable<Order>() }.getOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect order object")

        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)?.toInt()
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Server error")
        val username = principal?.getClaim("username", String::class)
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Server error")

        val orderToStore = order.copy(
            username = username,
            userId = userId,
            status = "created",
            checked = 1
        )

        val storedOrder = warehouseOrdersLocalRepository.insertNewOrder(orderToStore)

        if (storedOrder == null){
            call.respond(HttpStatusCode.BadRequest, "Order already exists")
        }
        else {
            call.respond(HttpStatusCode.OK, storedOrder)
        }
    }
}