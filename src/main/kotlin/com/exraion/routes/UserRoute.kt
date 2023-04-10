package com.exraion.routes

import com.exraion.data.repositories.user.UserRepository
import com.exraion.middleware.Middleware
import com.exraion.model.favorite.FavoriteBody
import com.exraion.model.history.HistoryUpdateStarsGiven
import com.exraion.model.order.OrderBody
import com.exraion.model.user.UserBody
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class UserRoute(
    private val repository: UserRepository,
    private val middleware: Middleware,
) {

    private fun Route.getDetailUser() {
        authenticate {
            get("/user") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { repository.getDetailUser(uid) }
            }
        }
    }

    private fun Route.updateUser() {
        authenticate {
            put("/user") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val body = call.receive<UserBody>()
                call.buildSuccessJson { repository.updateUser(uid, body) }
            }
        }
    }

    private fun Route.updateUserAvatar() {
        authenticate {
            put("/user/avatar") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val multipart = call.receiveMultipart()

                try {
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            repository.updateUserAvatar(uid, part)
                        }
                    }
                    call.buildSuccessJson { "Avatar updated" }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, "Error while uploading image")
                }
            }
        }
    }

    private fun Route.postFavorite() {
        authenticate {
            post("/user/favorite") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val body = call.receive<FavoriteBody>()
                call.buildSuccessJson { repository.insertFavorite(uid, body.menuId) }
            }
        }
    }

    private fun Route.deleteFavorite() {
        authenticate {
            delete("/user/favorite/{menuId}") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val menuId = call.parameters["menuId"] ?: ""
                call.buildSuccessJson { repository.deleteFavorite(uid, menuId) }
            }
        }
    }

    private fun Route.postOrder() {
        authenticate {
            post("/user/order") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val body = call.receive<OrderBody>()
                repository.insertOrder(uid, body)
                call.buildSuccessJson { "Thank you for the order!" }
            }
        }
    }

    private fun Route.getOrderHistory() {
        authenticate {
            get("/user/order") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { repository.getOrderHistory(uid) }
            }
        }
    }

    private fun Route.cancelOrder() {
        authenticate {
            put("/user/order/{orderId}/cancel") {
                middleware.apply { call.validateToken() }
                val orderId = call.parameters["orderId"] ?: ""
                repository.cancelOrder(orderId)
                call.buildSuccessJson { "Order cancelled" }
            }
        }
    }

    private fun Route.updateOrderStars() {
        authenticate {
            put("/user/order/{orderId}/rating") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val orderId = call.parameters["orderId"] ?: ""
                val stars = call.receive<HistoryUpdateStarsGiven>()
                repository.updateOrderStars(uid, orderId, stars.starsGiven)
                call.buildSuccessJson { "Order stars updated" }
            }
        }
    }

    fun Route.initRoute() {
        getDetailUser()
        updateUser()
        updateUserAvatar()
        postFavorite()
        deleteFavorite()
        postOrder()
        getOrderHistory()
        cancelOrder()
        updateOrderStars()
    }

}