package ru.dikoresearch.routes.orders.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.entities.responses.ListOfAllOrdersResponse
import ru.dikoresearch.domain.repository.local.WarehouseOrdersLocalRepository

fun Route.allOrders(
    warehouseOrdersLocalRepository: WarehouseOrdersLocalRepository
){
    get("all"){
        val orders = warehouseOrdersLocalRepository.getAllOrders()

        //Responds list of orders without images
        call.respond(HttpStatusCode.OK, ListOfAllOrdersResponse(orders = orders))
    }
}