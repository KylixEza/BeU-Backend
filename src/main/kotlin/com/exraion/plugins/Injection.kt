package com.exraion.plugins

import com.exraion.di.databaseModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureInjection() {
    install(Koin) {
        modules(
            databaseModule
        )
    }
}