package org.ppfc.api.security.auth

import org.ppfc.api.common.StringResource

sealed class AuthError(val message: String) {
    class NotAuthorized(message: String) : AuthError("${StringResource.notAuthorized}: $message")
    class InternalError(message: String? = null) : AuthError("${StringResource.internalError}: $message")
}
