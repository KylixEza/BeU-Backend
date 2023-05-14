package com.exraion.routes

import com.exraion.data.repositories.menu.MenuRepository
import com.exraion.middleware.Middleware
import com.exraion.model.ingredient.IngredientBody
import com.exraion.model.menu.MenuBody
import com.exraion.model.step.StepBody
import com.exraion.model.tool.ToolBody
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

    private fun Route.postStep() {
        post("/menu/{menuId}/step") {
            val menuId = call.parameters["menuId"] ?: ""
            val body = call.receive<StepBody>()
            menuRepository.insertStep(menuId, body.step)
            call.buildSuccessJson { "Step successfully added" }
        }
    }

    private fun Route.postIngredient() {
        post("/menu/{menuId}/ingredient") {
            val menuId = call.parameters["menuId"] ?: ""
            val body = call.receive<IngredientBody>()
            menuRepository.insertIngredient(menuId, body)
            call.buildSuccessJson { "Ingredient successfully added" }
        }
    }

    private fun Route.postTool() {
        post("/menu/{menuId}/tool") {
            val menuId = call.parameters["menuId"] ?: ""
            val body = call.receive<ToolBody>()
            menuRepository.insertTool(menuId, body.tool)
            call.buildSuccessJson { "Tool successfully added" }
        }
    }

    private fun Route.getMenus() {
        authenticate {
            get("/menu") {
                middleware.apply { call.validateToken() }

                val uid = middleware.getClaim(call, "uid") ?: ""
                val query = call.request.queryParameters["query"]
                val category = call.request.queryParameters["category"]
                val menus = if (query != null) {
                    menuRepository.getMenusBySearch(uid, query)
                } else if (category != null) {
                    menuRepository.getCategorizedMenus(uid, category)
                } else {
                    menuRepository.getRandomMenus(uid)
                }
                call.buildSuccessListJson { menus }
            }
        }
    }

    private fun Route.getDietMenus() {
        authenticate {
            get("/menu/diet") {
                middleware.apply { call.validateToken() }

                val uid = middleware.getClaim(call, "uid") ?: ""
                val menus = menuRepository.getDietMenus(uid)
                call.buildSuccessListJson { menus }
            }
        }
    }

    private fun Route.getExclusiveMenus() {
        authenticate {
            get("/menu/exclusive") {
                middleware.apply { call.validateToken() }

                val uid = middleware.getClaim(call, "uid") ?: ""
                val menus = menuRepository.getExclusiveMenus(uid)
                call.buildSuccessListJson { menus }
            }
        }
    }

    private fun Route.getDetailMenu() {
        authenticate {
            get("/menu/{menuId}") {
                middleware.apply { call.validateToken() }

                val uid = middleware.getClaim(call, "uid") ?: ""
                val menuId = call.parameters["menuId"] ?: ""
                val menu = menuRepository.getDetailMenu(uid, menuId)
                call.buildSuccessJson { menu }
            }
        }
    }

    private fun Route.getIngredients() {
        authenticate {
            get("/menu/{menuId}/ingredients") {
                middleware.apply { call.validateToken() }

                val menuId = call.parameters["menuId"] ?: ""
                val ingredients = menuRepository.getIngredients(menuId)
                call.buildSuccessListJson { ingredients }
            }
        }
    }

    fun Route.initRoute() {
        postMenu()
        postStep()
        postIngredient()
        postTool()
        getMenus()
        getDietMenus()
        getExclusiveMenus()
        getDetailMenu()
        getIngredients()
    }

}