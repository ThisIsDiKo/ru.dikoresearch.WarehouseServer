package ru.dikoresearch.data.repository.local

import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import ru.dikoresearch.data.tables.DatabaseFactory.dbQuery
import ru.dikoresearch.data.tables.ImagesTable
import ru.dikoresearch.data.tables.OrdersTable
import ru.dikoresearch.data.tables.UsersTable
import ru.dikoresearch.domain.entities.models.RemoteGoods
import ru.dikoresearch.domain.entities.models.Order
import ru.dikoresearch.domain.entities.models.OrderImage
import ru.dikoresearch.domain.entities.models.User
import ru.dikoresearch.domain.repository.local.WarehouseImagesLocalRepository
import ru.dikoresearch.domain.repository.local.WarehouseOrdersLocalRepository
import ru.dikoresearch.domain.repository.local.WarehouseUsersLocalRepository
import java.time.LocalDateTime

class WarehouseLocalRepositoryImpl: WarehouseUsersLocalRepository, WarehouseOrdersLocalRepository,
    WarehouseImagesLocalRepository {
    override suspend fun getUserByUserName(username: String): User? = dbQuery {
        UsersTable.select {
            UsersTable.username eq username
        }.map(::resultRowToUser).singleOrNull()
    }

    override suspend fun insertNewUser(user: User): User? = dbQuery{
        if (getUserByUserName(user.username) != null) return@dbQuery null

        val insertStatement = UsersTable.insert {
            it[UsersTable.username] = user.username
            it[UsersTable.password] = user.password
            it[UsersTable.salt] = user.salt
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun getAllOrders(): List<Order> = dbQuery {
        (OrdersTable innerJoin UsersTable)
            .selectAll()
            .map(::resultRowToOrderWithoutImages)
    }

    override suspend fun getOrderByOrderName(orderName: String): Order? = dbQuery{
        (OrdersTable innerJoin UsersTable)
            .select { OrdersTable.orderName eq orderName }
            .map(::resultRowToOrderWithoutImages)
            .singleOrNull()
            ?.let{ order ->
                val images: List<String> = ImagesTable
                    .select { ImagesTable.orderId eq  order.orderId }
                    .map { it[ImagesTable.imageName] }

                order.copy(images = images)
            }
    }

    override suspend fun insertNewOrder(order: Order): Order? = dbQuery{
        if (getOrderByOrderName(order.orderName) != null) return@dbQuery null

        val insertStatement = OrdersTable.insert {
            it[OrdersTable.userId] = order.userId
            it[OrdersTable.orderName] = order.orderName
            it[OrdersTable.orderStatus] = order.status
            it[OrdersTable.orderComment] = order.comment
            it[OrdersTable.createdAt] = LocalDateTime.now()
            it[OrdersTable.uuid] = order.uuid
            it[OrdersTable.goods] = Json.encodeToString(order.goods)
            it[OrdersTable.checked] = order.checked
        }

        val order = insertStatement.resultedValues?.singleOrNull()?.let{row ->
            val user = UsersTable.select { UsersTable.userId eq order.userId }.map(::resultRowToUser).singleOrNull()
            val goods: List<RemoteGoods> = Json.decodeFromString(row[OrdersTable.goods])

            Order(
                orderId = row[OrdersTable.orderId],
                orderName = row[OrdersTable.orderName],
                username = user?.username ?: "",
                userId = row[OrdersTable.userId],
                status = row[OrdersTable.orderStatus],
                createdAt = row[OrdersTable.createdAt].toString(),
                comment = row[OrdersTable.orderComment],
                images = emptyList(),
                uuid = row[OrdersTable.uuid],
                goods = goods,
                checked = row[OrdersTable.checked],
            )
        }
        exposedLogger.info("Found order in $order")
        order
    }

    override suspend fun getImagesByOrderId(orderId: Int): List<OrderImage> = dbQuery{
        ImagesTable
            .select { ImagesTable.orderId eq  orderId }
            .map(::resultRowToOrderImage)
    }

    override suspend fun getImageByImageName(imageName: String): OrderImage? = dbQuery{
        ImagesTable
            .select { ImagesTable.imageName eq imageName }
            .map(::resultRowToOrderImage)
            .singleOrNull()
    }

    override suspend fun insertImage(image: OrderImage): OrderImage? = dbQuery{
        if (getImageByImageName(image.imageName) != null) return@dbQuery null

        val insertStatement = ImagesTable.insert {
            it[ImagesTable.orderId] = image.orderId
            it[ImagesTable.imageName] = image.imageName
            it[ImagesTable.imagePath] = image.imagePath
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToOrderImage)
    }

    //Mappers
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UsersTable.userId],
        username = row[UsersTable.username],
        password = row[UsersTable.password],
        salt = row[UsersTable.salt],
    )

    private fun resultRowToOrderWithoutImages(row: ResultRow): Order {
        val goods: List<RemoteGoods> = try {
            Json.decodeFromString(row[OrdersTable.goods])
        } catch (e: Exception) {
            org.jetbrains.exposed.sql.exposedLogger.error("Got exception while decoding goods from table $e")
            emptyList()
        }

        return Order(
            orderId = row[OrdersTable.orderId],
            orderName = row[OrdersTable.orderName],
            username = row[UsersTable.username],
            userId = row[UsersTable.userId],
            status = row[OrdersTable.orderStatus],
            createdAt = row[OrdersTable.createdAt].toString(),
            comment = row[OrdersTable.orderComment],
            images = emptyList(),
            uuid = row[OrdersTable.uuid],
            goods = goods,
            checked = row[OrdersTable.checked],
        )
    }

    private fun resultRowToOrderImage(row: ResultRow): OrderImage {
        return OrderImage(
            orderId = row[ImagesTable.orderId],
            imageName = row[ImagesTable.imageName],
            imagePath = row[ImagesTable.imagePath]
        )
    }
}