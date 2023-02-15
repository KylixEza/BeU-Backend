package com.exraion.plugins

import com.exraion.routes.AuthRoute
import com.exraion.routes.UserRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val authRoute by inject<AuthRoute>()
    val userRoute by inject<UserRoute>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authRoute.apply { this@routing.initRoute() }
        userRoute.apply { this@routing.initRoute() }
    }
}
