package org.ppfc.api.security.auth

interface AuthProvider {
    suspend fun auth(username: String, password: String): AuthResult

    suspend fun authChallengeSetNewPassword(
        username: String,
        password: String,
        session: String
    ): AuthChallengeSetNewPasswordResult

    suspend fun refreshAccessToken(refreshToken: String): RefreshAccessTokenResult
}