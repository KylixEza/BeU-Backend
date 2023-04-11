package com.exraion

import com.exraion.data.repositories.daily_xp.DailyXpRepository
import com.exraion.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT").toInt(),
        host = if (System.getenv("ENV") == "DEV") "localhost" else "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureInjection()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureFiretor()

    initializedData()
}

fun Application.initializedData() {
    val repository by inject<DailyXpRepository>()

    CoroutineScope(Dispatchers.IO).launch {
        repository.insertDailyXp()
    }
}
