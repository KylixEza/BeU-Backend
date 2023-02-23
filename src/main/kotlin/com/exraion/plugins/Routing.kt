package com.exraion.plugins

import com.exraion.routes.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val authRoute by inject<AuthRoute>()
    val userRoute by inject<UserRoute>()
    val menuRoute by inject<MenuRoute>()
    val voucherRoute by inject<VoucherRoute>()
    val leaderboardRoute by inject<LeaderboardRoute>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authRoute.apply { this@routing.initRoute() }
        userRoute.apply { this@routing.initRoute() }
        menuRoute.apply { this@routing.initRoute() }
        voucherRoute.apply { this@routing.initRoute() }
        leaderboardRoute.apply { this@routing.initRoute() }
    }
}
