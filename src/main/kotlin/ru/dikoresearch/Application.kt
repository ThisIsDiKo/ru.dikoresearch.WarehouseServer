package ru.dikoresearch

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import ru.dikoresearch.data.repository.local.ImageFileManagerImpl
import ru.dikoresearch.data.repository.local.WarehouseLocalRepositoryImpl
import ru.dikoresearch.data.repository.remote.OrdersRemoteRepositoryImpl
import ru.dikoresearch.data.tables.DatabaseFactory
import ru.dikoresearch.domain.security.hashing.SHA256HashingService
import ru.dikoresearch.domain.security.token.JwtTokenService
import ru.dikoresearch.domain.security.token.TokenConfig
import ru.dikoresearch.plugins.configureRouting
import ru.dikoresearch.routes.configureMonitoring
import ru.dikoresearch.routes.configureSecurity
import ru.dikoresearch.routes.configureSerialization

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val hashingService = SHA256HashingService()
    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = environment.config.property("jwt.expiresIn").getString().toLong(),
        secret = environment.config.property("jwt.secret").getString()
    )

    val dbUrl = environment.config.property("database.url").getString()
    val dbUsername = environment.config.property("database.username").getString()
    val dbPassword = environment.config.property("database.password").getString()

    val imagedStoragePath = environment.config.property("files.imagesStorage").getString()

    val remoteServerUrl = environment.config.property("remote.remoteServerUrl").getString()

    val httpClient = HttpClient(CIO){
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation){

        }
    }
    //TODO get username and password for db from config file
    DatabaseFactory.init(
        url = dbUrl,
        username = dbUsername,
        password = dbPassword
    )

    val warehouseLocalRepository = WarehouseLocalRepositoryImpl()
    val ordersRemoteRepository = OrdersRemoteRepositoryImpl(
        remoteServerUrl = remoteServerUrl,
        client = httpClient
    )
    val imageFileManager = ImageFileManagerImpl(imagedStoragePath)

    configureSerialization()
    configureMonitoring()

    //TODO Config routing
    configureRouting(
        warehouseLocalRepository = warehouseLocalRepository,
        ordersRemoteRepository = ordersRemoteRepository,
        imageFileManager = imageFileManager,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
    )

    configureSecurity(tokenConfig)

}