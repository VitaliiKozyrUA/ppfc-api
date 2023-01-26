package org.ppfc.api.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ppfc.api.common.StringResource

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(StringResource.welcomeToPpfcServer)
        }
    }
}
