package com.exraion.plugins

import com.kylix.Firetor
import io.ktor.server.application.*

fun Application.configureFiretor() {
    install(Firetor) {
        setAdminKey("makaroni-beu-admin-key.json")
        enableFirebaseStorage("makaroni-beu.appspot.com")
    }
}