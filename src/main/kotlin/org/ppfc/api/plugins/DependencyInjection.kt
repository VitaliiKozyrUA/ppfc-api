package org.ppfc.api.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.ppfc.api.appModule

fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(appModule)
    }
}
