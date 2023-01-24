package org.ppfc.api.model.service.course

import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse(
    val id: Long,
    val number: Long
)