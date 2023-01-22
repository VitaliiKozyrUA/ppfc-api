package org.ppfc.api.security.auth

sealed class AuthResult {
    class Success(val accessToken: String, val refreshToken: String) : AuthResult()
    class Failure(val error: AuthError) : AuthResult()
    class NewPasswordRequired(val session: String) : AuthResult()
}