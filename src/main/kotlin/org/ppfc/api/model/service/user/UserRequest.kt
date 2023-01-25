package org.ppfc.api.model.service.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val userCode: String,
    val groupId: Long?,
    val teacherId: Long?
)