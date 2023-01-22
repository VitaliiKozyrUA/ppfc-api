package org.ppfc.api.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.ppfc.api.data.config.ConfigProvider
import org.ppfc.api.model.auth.request.AuthChallengeSetNewPasswordRequest
import org.ppfc.api.model.auth.request.AuthenticationRequest
import org.ppfc.api.model.auth.request.RefreshAccessTokenRequest
import org.ppfc.api.model.auth.response.AuthResponse
import org.ppfc.api.model.auth.response.AuthResponseStatus
import org.ppfc.api.security.auth.*
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val config: ConfigProvider = get()
    val authProvider: AuthProvider by inject()

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

    routing {
        post("/authenticate") {
            val authenticationRequest = call.receive<AuthenticationRequest>()

            when (
                val result = authProvider.auth(
                    username = authenticationRequest.username,
                    password = authenticationRequest.password
                )
            ) {
                is AuthResult.Success -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = AuthResponse(
                            status = AuthResponseStatus.SUCCESS,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken
                        )
                    )
                }

                is AuthResult.Failure -> {
                    when (result.error) {
                        is AuthError.InternalError -> {
                            call.respond(
                                status = HttpStatusCode.InternalServerError,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }

                        is AuthError.NotAuthorized -> {
                            call.respond(
                                status = HttpStatusCode.Unauthorized,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }
                    }
                }

                is AuthResult.NewPasswordRequired -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = AuthResponse(
                            status = AuthResponseStatus.NEW_PASSWORD_REQUIRED,
                            session = result.session
                        )
                    )
                }
            }
        }

        post("/authChallengeSetNewPassword") {
            val challengeRequest = call.receive<AuthChallengeSetNewPasswordRequest>()

            when (
                val result = authProvider.authChallengeSetNewPassword(
                    username = challengeRequest.username,
                    password = challengeRequest.password,
                    session = challengeRequest.session
                )
            ) {
                is AuthChallengeSetNewPasswordResult.Success -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = AuthResponse(
                            status = AuthResponseStatus.SUCCESS,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken
                        )
                    )
                }

                is AuthChallengeSetNewPasswordResult.Failure -> {
                    when (result.error) {
                        is AuthError.InternalError -> {
                            call.respond(
                                status = HttpStatusCode.InternalServerError,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }

                        is AuthError.NotAuthorized -> {
                            call.respond(
                                status = HttpStatusCode.Unauthorized,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }
                    }
                }
            }
        }

        post("/refreshAccessToken") {
            val refreshAccessTokenRequest = call.receive<RefreshAccessTokenRequest>()

            when (
                val result = authProvider.refreshAccessToken(
                    refreshToken = refreshAccessTokenRequest.refreshToken
                )
            ) {
                is RefreshAccessTokenResult.Success -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = AuthResponse(
                            status = AuthResponseStatus.SUCCESS,
                            accessToken = result.accessToken
                        )
                    )
                }

                is RefreshAccessTokenResult.Failure -> {
                    when (result.error) {
                        is AuthError.InternalError -> {
                            call.respond(
                                status = HttpStatusCode.InternalServerError,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }

                        is AuthError.NotAuthorized -> {
                            call.respond(
                                status = HttpStatusCode.Unauthorized,
                                message = AuthResponse(
                                    status = AuthResponseStatus.FAILURE,
                                    error = result.error.message
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}