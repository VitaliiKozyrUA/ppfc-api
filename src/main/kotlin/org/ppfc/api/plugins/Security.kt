package org.ppfc.api.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.get
import org.ppfc.api.data.config.ConfigProvider
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val config: ConfigProvider = get()

    val jwtIssuer = config.getConfig().jwtIssuer
    val jwtRealm = config.getConfig().jwtRealm

    val jwkProvider = JwkProviderBuilder(jwtIssuer)
        .cached(10, 60, TimeUnit.MINUTES)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt {
            realm = jwtRealm

            verifier(jwkProvider, jwtIssuer) {
                acceptLeeway(3)
            }

            validate { credential ->
                JWTPrincipal(credential.payload)
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Токен недійсний або термін його дії минув.")
            }
        }
    }
}