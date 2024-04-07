package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
        basic("basic-auth") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == "test" && credentials.password == "test") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

}
