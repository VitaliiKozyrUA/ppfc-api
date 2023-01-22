package org.ppfc.api.model.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val status: AuthResponseStatus,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val session: String? = null,
    val error: String? = null
)
