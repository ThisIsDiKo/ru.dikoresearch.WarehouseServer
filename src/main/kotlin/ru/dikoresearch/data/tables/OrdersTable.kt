package ru.dikoresearch.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OrdersTable: Table("orders") {
    val orderId = integer("order_id").autoIncrement()
    val userId = (integer("user_id") references UsersTable.userId)
    val orderName = varchar("order_name", length = 255)
    val orderStatus = varchar("order_status", length = 255)
    val orderComment = varchar("comment", length = 255)
    val createdAt = datetime("created_at")
    val uuid = varchar("uuid", length = 255)
    val goods = text("goods")
    val checked = integer("checked")

    override val primaryKey = PrimaryKey(orderId)
}