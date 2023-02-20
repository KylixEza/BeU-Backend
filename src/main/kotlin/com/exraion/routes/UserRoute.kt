package com.exraion.routes

import com.exraion.data.firebase.FirebaseStorageUrl
import com.exraion.data.firebase.FirebaseStorageUrl.getDownloadUrl
import com.exraion.data.firebase.FirebaseStorageUrl.reference
import com.exraion.data.repositories.user.UserRepository
import com.exraion.middleware.Middleware
import com.exraion.model.favorite.FavoriteBody
import com.exraion.model.order.OrderBody
import com.exraion.model.review.ReviewBody
import com.exraion.model.user.UserBody
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.util.convert
import com.google.firebase.cloud.StorageClient
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
    private val bucket = StorageClient.getInstance().bucket()

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
                var urlPath = ""

                try {
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val (fileName, fileBytes) = part.convert(compressQuality = 0.5f)
                            bucket.create("avatar_path/$uid/$fileName", fileBytes, "image/png")
                            urlPath = FirebaseStorageUrl
                                .basePath
                                .reference("avatar_path")
                                .reference(uid)
                                .getDownloadUrl(fileName)
                        }
                    }
                    repository.updateUserAvatar(uid, urlPath)
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

    private fun Route.postReview() {
        authenticate {
            post("/user/review") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val body = call.receive<ReviewBody>()
                repository.insertReview(uid, body)
                call.buildSuccessJson { "Thank you for the review!" }
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

    private fun Route.getHistories() {
        authenticate {
            get("/user/history") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { repository.getHistories(uid) }
            }
        }
    }

    fun Route.initRoute() {
        getDetailUser()
        updateUser()
        updateUserAvatar()
        postFavorite()
        deleteFavorite()
        postReview()
        postOrder()
        getHistories()
    }

}