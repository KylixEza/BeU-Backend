package com.exraion.routes

import com.exraion.data.repositories.leaderboard.LeaderboardRepository
import com.exraion.middleware.Middleware
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.routes.RouteResponseHelper.buildSuccessListJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class LeaderboardRoute(
    private val leaderboardRepository: LeaderboardRepository,
    private val middleware: Middleware
) {

    private fun Route.getLeaderboard() {
        authenticate {
            get("/leaderboard") {
                middleware.apply { call.validateToken() }
                call.buildSuccessListJson { leaderboardRepository.getLeaderboard() }
            }
        }
    }

    private fun Route.getUserRank() {
        authenticate {
            get("/leaderboard/me") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { leaderboardRepository.getUserRank(uid) }
            }
        }
    }

    fun Route.initRoute() {
        getLeaderboard()
        getUserRank()
    }

}