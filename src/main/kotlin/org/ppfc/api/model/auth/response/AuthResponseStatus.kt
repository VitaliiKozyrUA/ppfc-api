package org.ppfc.api.model.auth.response

import kotlinx.serialization.Serializable

@Serializable
enum class AuthResponseStatus {
    SUCCESS,
    FAILURE,
    NEW_PASSWORD_REQUIRED
}

