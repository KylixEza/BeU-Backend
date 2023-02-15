package com.exraion.data.repositories.token

interface TokenRepository {
    suspend fun insertToBlacklist(token: String)
    suspend fun isTokenValid(token: String?): Boolean
}