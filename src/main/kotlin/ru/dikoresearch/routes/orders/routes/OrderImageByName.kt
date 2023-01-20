package ru.dikoresearch.routes.orders.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dikoresearch.domain.repository.local.ImageFileManager
import ru.dikoresearch.domain.repository.local.WarehouseImagesLocalRepository

fun Route.orderImageByName(
    warehouseImagesLocalRepository: WarehouseImagesLocalRepository,
    imageFileManager: ImageFileManager
) {
    get("image/{imageName}"){
        val imageName = call.parameters["imageName"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val image = warehouseImagesLocalRepository.getImageByImageName(imageName)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Image doesn't exist")

        val file = imageFileManager.loadImageFromDisk(image.imagePath)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Image doesn't exist")

        call.respondFile(file)
    }
}