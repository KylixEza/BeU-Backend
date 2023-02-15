package com.exraion.routes

import com.exraion.data.repositories.user.UserRepository
import com.exraion.middleware.Middleware
import com.exraion.model.auth.LoginBody
import com.exraion.model.auth.RegisterBody
import com.exraion.model.auth.TokenResponse
import com.exraion.routes.RouteResponseHelper.buildErrorJson
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.security.hashing.SaltedHash
import com.exraion.security.token.TokenClaim
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

class AuthRoute(
    private val repository: UserRepository,
    private val middleware: Middleware,
) {

    private fun Route.signUp() {
        post("/signup") {
            val body = call.receive<RegisterBody>()

            val saltedHash = middleware.hashPassword(body.password)

            val isEmailExist = repository.isEmailExist(body.email)
            if (isEmailExist) {
                call.buildErrorJson(message = "email already in use")
                return@post
            }

            val user = repository.insertUser(body, saltedHash)
            val token = middleware.generateToken(TokenClaim("uid", user.uid),)

            call.buildSuccessJson { TokenResponse(token) }
        }
    }

    private fun Route.signIn() {
        post("/signin") {
            val body = call.receive<LoginBody>()

            val user = repository.getUserByEmail(body.email)
            if (user == null) {
                call.buildErrorJson(message = "user not found")
                return@post
            }

            middleware.apply { call.verifyPassword(body.password, SaltedHash(
                hash = user.password,
                salt = user.salt
            )
            ) }

            val token = middleware.generateToken(TokenClaim("uid", user.uid))

            call.buildSuccessJson { TokenResponse(token) }
        }
    }

    private fun Route.signOut() {
        post("/signout") {
            val jwt = call.request.header("Authorization")?.substring("Bearer ".length)
            middleware.apply {
                application.invalidateToken(jwt ?: "")
            }
            call.buildSuccessJson { "Sign out success" }
        }
    }

    fun Route.initRoute() {
        signUp()
        signIn()
        signOut()
    }
}