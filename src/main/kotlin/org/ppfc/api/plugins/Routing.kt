package org.ppfc.api.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticate {
            get("/") {
                call.respondText("Welcome to PPFC Api Server!")
            }
        }
    }
}
