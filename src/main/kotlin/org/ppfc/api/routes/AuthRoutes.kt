package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.model.auth.request.AuthChallengeSetNewPasswordRequest
import org.ppfc.api.model.auth.request.AuthenticationRequest
import org.ppfc.api.model.auth.request.RefreshAccessTokenRequest
import org.ppfc.api.model.auth.response.AuthResponse
import org.ppfc.api.model.auth.response.AuthResponseStatus
import org.ppfc.api.security.auth.*

fun Route.authRouting() {
    val authProvider: AuthProvider by inject()

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