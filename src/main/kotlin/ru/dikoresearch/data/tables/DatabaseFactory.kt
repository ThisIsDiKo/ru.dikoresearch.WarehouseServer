package ru.dikoresearch.data.tables

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {

    fun init(url: String, username: String, password: String){
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val database = Database.connect(
            url = url,
            driver = driverClassName,
            user = username,
            password = password
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}