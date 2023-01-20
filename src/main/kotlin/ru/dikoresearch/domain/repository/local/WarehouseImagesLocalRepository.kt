package ru.dikoresearch.domain.repository.local

import ru.dikoresearch.domain.entities.models.OrderImage

interface WarehouseImagesLocalRepository {
    suspend fun getImagesByOrderId(orderId: Int): List<OrderImage>
    suspend fun getImageByImageName(imageName: String): OrderImage?
    suspend fun insertImage(image: OrderImage): OrderImage?
}