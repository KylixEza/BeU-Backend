package com.exraion.di

import com.exraion.middleware.Middleware
import com.exraion.security.hashing.HashingService
import com.exraion.security.hashing.SHA256HashingService
import com.exraion.security.token.JWTTokenService
import com.exraion.security.token.TokenService
import org.koin.dsl.module

val securityModule = module {
    single<HashingService> { SHA256HashingService() }
    single<TokenService> { JWTTokenService() }
}

val middlewareModule = module {
    single { Middleware(get(), get(), get()) }
}