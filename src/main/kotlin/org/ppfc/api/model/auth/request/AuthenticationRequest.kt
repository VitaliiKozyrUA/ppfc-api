package org.ppfc.api.model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(
    val username: String,
    val password: String
)
