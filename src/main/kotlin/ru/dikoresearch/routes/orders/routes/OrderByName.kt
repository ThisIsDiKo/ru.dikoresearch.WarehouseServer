package ru.dikoresearch.routes.orders.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.entities.models.Order
import ru.dikoresearch.domain.repository.local.WarehouseOrdersLocalRepository
import ru.dikoresearch.domain.repository.remote.OrdersRemoteRepository

fun Route.orderByName(
    warehouseOrdersLocalRepository: WarehouseOrdersLocalRepository,
    ordersRemoteRepository: OrdersRemoteRepository
){
    get("name/{name}"){
        val orderName = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val formattedName = orderName.split("-").last().trimStart{ it == '0'}

        val order = warehouseOrdersLocalRepository.getOrderByOrderName(formattedName)

        if (order == null){

            val remoteOrderInfo = ordersRemoteRepository.getOrderByOrderName(formattedName)
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Order not found on remote server")

            val receivedOrder = Order(
                orderId = -1,
                orderName = formattedName,
                userId = -1,
                username = "",
                status = "New",
                createdAt = "",
                comment = "",
                images = emptyList(),
                uuid = remoteOrderInfo.uuid,
                goods = remoteOrderInfo.goods,
                checked = 0
            )

            call.respond(HttpStatusCode.OK, receivedOrder)
        }
        else {
            call.respond(HttpStatusCode.OK, order)
        }
    }
}