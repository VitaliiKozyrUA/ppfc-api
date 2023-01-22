package org.ppfc.api.security.auth

sealed class AuthChallengeSetNewPasswordResult {
    class Success(val accessToken: String, val refreshToken: String) : AuthChallengeSetNewPasswordResult()
    class Failure(val error: AuthError) : AuthChallengeSetNewPasswordResult()
}
