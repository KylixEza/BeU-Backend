package com.exraion.routes

import com.exraion.data.repositories.daily_xp.DailyXpRepository
import com.exraion.middleware.Middleware
import com.exraion.model.daily_xp.DailyXpRequest
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.routes.RouteResponseHelper.buildSuccessListJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

class DailyXpRoute(
    private val repository: DailyXpRepository,
    private val middleware: Middleware
) {
    private fun Route.checkDailyXpAvailability() {
        authenticate {
            get("/daily-xp/check") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson(
                    "Daily XP Checked",
                ) { repository.checkDailyXpAvailability(uid) }
            }
        }
    }

    private fun Route.getDailyXps() {
        authenticate {
            get("/daily-xp") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessListJson { repository.getDailXps(uid) }
            }
        }
    }

    private fun Route.getTodayDailyXp() {
        authenticate {
            get("/daily-xp/today") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { repository.getTodayDailyXP(uid) }
            }
        }
    }

    private fun Route.takeDailyXp() {
        authenticate {
            post("/daily-xp/take") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val body = call.receive<DailyXpRequest>()
                call.buildSuccessJson(
                    "Daily XP Taken",
                ) { repository.takeDailyXp(uid, body) }
            }
        }
    }

    fun Route.initRoute() {
        checkDailyXpAvailability()
        getDailyXps()
        getTodayDailyXp()
        takeDailyXp()
    }

}