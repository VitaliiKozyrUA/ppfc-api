package org.ppfc.api.model.service.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val id: Long,
    val groupId: Long?,
    val teacherId: Long?
)