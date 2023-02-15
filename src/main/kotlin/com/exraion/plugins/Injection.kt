package com.exraion.plugins

import com.exraion.di.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureInjection() {
    install(Koin) {
        modules(
            databaseModule,
            repositoryModule,
            securityModule,
            middlewareModule,
            routeModule
        )
    }
}