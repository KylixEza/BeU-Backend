package com.exraion.routes

import com.exraion.data.repositories.menu.MenuRepository
import com.exraion.middleware.Middleware
import com.exraion.model.menu.MenuBody
import com.exraion.routes.RouteResponseHelper.buildErrorJson
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.routes.RouteResponseHelper.buildSuccessListJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

class MenuRoute(
    private val menuRepository: MenuRepository,
    private val middleware: Middleware,
) {

    private fun Route.postMenu() {
        post("/menu") {
            val body = try {
                call.receive<MenuBody>()
            } catch (e: Exception) {
                call.buildErrorJson(e)
                return@post
            }
            menuRepository.insertMenu(body)
            call.buildSuccessJson { "Menu successfully added" }
        }
    }

    private fun Route.getRandomMenus() {
        authenticate {
            get("/menu") {
                middleware.apply { call.validateToken() }

                val uid = middleware.getClaim(call, "uid") ?: ""
                val menus = menuRepository.getRandomMenus(uid)
                call.buildSuccessListJson { menus }
            }
        }
    }

    fun Route.initRoute() {
        postMenu()
        getRandomMenus()
    }

}