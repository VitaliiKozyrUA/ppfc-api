package org.ppfc.api.security.auth

sealed class AuthError(val message: String) {
    class NotAuthorized(message: String) : AuthError("Not authorized: $message")
    class InternalError(message: String? = null) : AuthError("Internal error: $message")
}
