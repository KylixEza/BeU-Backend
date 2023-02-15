package com.exraion

import com.exraion.data.firebase.FirebaseAdmin
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.exraion.plugins.*

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT").toInt(),
        host = if (System.getenv("ENV") == "DEV") "localhost" else "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    FirebaseAdmin.init()
    configureInjection()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
