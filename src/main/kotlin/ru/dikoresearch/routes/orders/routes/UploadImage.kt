package ru.dikoresearch.routes.orders.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.dikoresearch.domain.entities.models.OrderImage
import ru.dikoresearch.domain.repository.local.ImageFileManager
import ru.dikoresearch.domain.repository.local.WarehouseImagesLocalRepository
import java.io.File

fun Route.uploadImage(
    warehouseImagesLocalRepository: WarehouseImagesLocalRepository,
    imageFileManager: ImageFileManager
) {
    post("uploadImage"){
        val multipartData = call.receiveMultipart()
        var imageName: String = "Unknown.jpeg"
        var orderId: Int = 1

        multipartData.forEachPart { part ->
            when(part){
                is PartData.FormItem -> {
                    when(part.name){
                        "orderId" -> {
                            orderId = part.value.substring(1, part.value.length-1).toInt()
                        }
                        "imageName" -> {
                            imageName = part.value.substring(1, part.value.length-1)
                        }
                        else -> {

                        }
                    }
                }
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()

                    val fileName = imageName

                    val orderName = imageName.split("_").getOrElse(1){"demo"}

                    val writeResult = imageFileManager.saveToDisk(
                        folderName = orderName,
                        fileName = fileName,
                        bytes = fileBytes
                    )

                    if (writeResult != null){
                        val orderImage = OrderImage(
                            orderId = orderId,
                            imageName = imageName,
                            imagePath = writeResult.absolutePath
                        )
                        call.application.environment.log.info("String image $orderImage")
                        val result = warehouseImagesLocalRepository.insertImage(orderImage)
                        call.respond(HttpStatusCode.OK)
                    }
                    else {
                        call.application.environment.log.error("Can't save file to disk")
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
                else -> {}
            }
        }
    }
}