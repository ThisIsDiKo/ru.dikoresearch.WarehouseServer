package ru.dikoresearch.data.tables

import org.jetbrains.exposed.sql.Table

object ImagesTable: Table("images") {
    val imageId = integer("image_id").autoIncrement()
    val orderId = (integer("order_id") references OrdersTable.orderId)
    val imageName = varchar("image_name", length = 255)
    val imagePath = varchar("image_path", length = 255)

    override val primaryKey = PrimaryKey(imageId)
}