package org.ppfc.api.model.service.classroom

import kotlinx.serialization.Serializable

@Serializable
data class ClassroomResponse(
    val id: Long,
    val name: String
)