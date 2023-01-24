package org.ppfc.api.model.service.group

import kotlinx.serialization.Serializable
import org.ppfc.api.model.service.course.CourseResponse

@Serializable
data class GroupResponse(
    val id: Long,
    val number: Long,
    val course: CourseResponse
)