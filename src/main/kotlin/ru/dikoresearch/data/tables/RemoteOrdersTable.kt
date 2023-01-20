package ru.dikoresearch.data.tables

import org.jetbrains.exposed.sql.Table

object RemoteOrdersTable: Table("aride_orders") {
    val remoteOrderId = integer("aride_order_id").autoIncrement()
    val orderId = (integer("order_id") references OrdersTable.orderId)
    val name = varchar("name", length = 255)
    val createdAt = varchar("created_at", length = 255)
    val uuid = varchar("uuid", length = 255)
    val goods = text("goods")

    override val primaryKey = PrimaryKey(remoteOrderId)
}