package ru.dikoresearch.data.repository.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.dikoresearch.domain.entities.models.RemoteOrder
import ru.dikoresearch.domain.repository.remote.OrdersRemoteRepository

class OrdersRemoteRepositoryImpl(
    private val remoteServerUrl: String,
    private val client: HttpClient
): OrdersRemoteRepository {

    override suspend fun getOrderByOrderName(orderName: String): RemoteOrder? {
        val remoteOrderResponse = client.get(remoteServerUrl+orderName)

        return if (remoteOrderResponse.status != HttpStatusCode.OK){
            println("Got response from remote server with code ${remoteOrderResponse.status}")
            null
        } else {
            createOrderFromRemoteResponse(remoteOrderResponse.body())
        }
    }

    private fun createOrderFromRemoteResponse(responseText: String): RemoteOrder?{
        return try {
            Json.decodeFromString<RemoteOrder>(responseText)

        } catch (e: Exception){
            println("Got exception while converting response from server $e")
            null
        }
    }
}