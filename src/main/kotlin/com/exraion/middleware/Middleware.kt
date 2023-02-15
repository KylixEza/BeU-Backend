package com.exraion.middleware

import com.exraion.data.repositories.token.TokenRepository
import com.exraion.routes.RouteResponseHelper.buildErrorJson
import com.exraion.security.hashing.HashingService
import com.exraion.security.hashing.SaltedHash
import com.exraion.security.token.TokenClaim
import com.exraion.security.token.TokenService
import com.exraion.util.Config.tokenConfig
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*

class Middleware(
    private val repository: TokenRepository,
    private val tokenService: TokenService,
    private val hashingService: HashingService
) {

    fun hashPassword(password: String) = hashingService.generateSaltedHash(password)

    suspend fun ApplicationCall.verifyPassword(password: String, salt: SaltedHash) {
        val isPasswordValid = hashingService.verify(password, salt)
        if (!isPasswordValid) {
            buildErrorJson(message = "Invalid password")
        }
    }

    fun generateToken(vararg claims: TokenClaim) = tokenService.generate(
        config = tokenConfig,
        claims = claims
    )

    suspend fun Application.invalidateToken(token: String) {
        tokenService.apply {
            invalidate(token) { repository.insertToBlacklist(this) }
        }
    }

    suspend fun ApplicationCall.validateToken() {
        val jwt = request.header("Authorization")?.substring("Bearer ".length)
        val isValid = repository.isTokenValid(jwt)
        if (!isValid) {
            buildErrorJson(httpStatusCode = Unauthorized, message = "Invalid token")
        }
    }

    inline fun<reified T: Any> getClaim(call: ApplicationCall, claimName: String) = kotlin.run {
        val principal = call.principal<JWTPrincipal>()
        principal?.getClaim(claimName, T::class)
    }

}