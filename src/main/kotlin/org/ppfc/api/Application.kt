package org.ppfc.api


import io.ktor.server.application.*
import org.ppfc.api.plugins.configureDependencyInjection
import org.ppfc.api.plugins.configureRouting
import org.ppfc.api.plugins.configureSecurity
import org.ppfc.api.plugins.configureSerialization

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureDependencyInjection()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
