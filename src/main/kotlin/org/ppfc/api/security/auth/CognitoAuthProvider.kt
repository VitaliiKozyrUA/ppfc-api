package org.ppfc.api.security.auth

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.ppfc.api.data.config.ConfigProvider
import org.ppfc.api.security.AppCredentialsProvider

class CognitoAuthProvider : AuthProvider, KoinComponent {
    private val config: ConfigProvider = get()

    override suspend fun auth(
        username: String, password: String
    ): AuthResult {
        val authParams = mutableMapOf<String, String>()
        authParams["USERNAME"] = username
        authParams["PASSWORD"] = password

        val authRequest = AdminInitiateAuthRequest {
            clientId = config.getConfig().awsClientId
            userPoolId = config.getConfig().awsUserPoolId
            authParameters = authParams
            authFlow = AuthFlowType.AdminUserPasswordAuth
        }

        try {
            CognitoIdentityProviderClient {
                region = config.getConfig().awsRegion
                credentialsProvider = AppCredentialsProvider()
            }.use { identityProviderClient ->
                val response = identityProviderClient.adminInitiateAuth(authRequest)
                val accessToken = response.authenticationResult?.accessToken
                val refreshToken = response.authenticationResult?.refreshToken

                if (response.challengeName == ChallengeNameType.NewPasswordRequired && response.session != null) {
                    return AuthResult.NewPasswordRequired(session = response.session!!)
                }

                if (accessToken == null || refreshToken == null) {
                    return AuthResult.Failure(
                        error = AuthError.InternalError()
                    )
                }

                return AuthResult.Success(
                    accessToken = accessToken, refreshToken = refreshToken
                )
            }
        } catch (e: NotAuthorizedException) {
            return AuthResult.Failure(
                error = AuthError.NotAuthorized(message = e.message.toString())
            )
        } catch (e: Exception) {
            return AuthResult.Failure(
                error = AuthError.InternalError(message = e.message)
            )
        }
    }

    override suspend fun authChallengeSetNewPassword(
        username: String, password: String, session: String
    ): AuthChallengeSetNewPasswordResult {
        val challengeResponseParameters = mutableMapOf<String, String>()
        challengeResponseParameters["USERNAME"] = username
        challengeResponseParameters["NEW_PASSWORD"] = password

        val authChallengeRequest = RespondToAuthChallengeRequest {
            challengeName = ChallengeNameType.NewPasswordRequired
            clientId = config.getConfig().awsClientId
            challengeResponses = challengeResponseParameters
            this.session = session
        }

        try {
            CognitoIdentityProviderClient {
                region = config.getConfig().awsRegion
            }.use { identityProviderClient ->
                val response = identityProviderClient.respondToAuthChallenge(authChallengeRequest)

                val accessToken = response.authenticationResult?.accessToken
                val refreshToken = response.authenticationResult?.refreshToken

                if (accessToken == null || refreshToken == null) {
                    return AuthChallengeSetNewPasswordResult.Failure(
                        error = AuthError.InternalError()
                    )
                }

                return AuthChallengeSetNewPasswordResult.Success(
                    accessToken = accessToken, refreshToken = refreshToken
                )
            }
        } catch (e: NotAuthorizedException) {
            return AuthChallengeSetNewPasswordResult.Failure(
                error = AuthError.NotAuthorized(message = e.message.toString())
            )
        } catch (e: Exception) {
            return AuthChallengeSetNewPasswordResult.Failure(
                error = AuthError.InternalError(message = e.message)
            )
        }
    }

    override suspend fun refreshAccessToken(
        refreshToken: String
    ): RefreshAccessTokenResult {
        val authParams = mutableMapOf<String, String>()
        authParams["REFRESH_TOKEN"] = refreshToken

        val authRequest = AdminInitiateAuthRequest {
            clientId = config.getConfig().awsClientId
            userPoolId = config.getConfig().awsUserPoolId
            authParameters = authParams
            authFlow = AuthFlowType.RefreshTokenAuth
        }

        try {
            CognitoIdentityProviderClient {
                region = config.getConfig().awsRegion
                credentialsProvider = AppCredentialsProvider()
            }.use { identityProviderClient ->
                val response = identityProviderClient.adminInitiateAuth(authRequest)
                val accessToken = response.authenticationResult?.accessToken ?: run {
                    return RefreshAccessTokenResult.Failure(
                        error = AuthError.InternalError()
                    )
                }

                return RefreshAccessTokenResult.Success(
                    accessToken = accessToken
                )
            }
        } catch (e: NotAuthorizedException) {
            return RefreshAccessTokenResult.Failure(
                error = AuthError.NotAuthorized(message = e.message.toString())
            )
        } catch (e: Exception) {
            return RefreshAccessTokenResult.Failure(
                error = AuthError.InternalError(message = e.message)
            )
        }
    }
}