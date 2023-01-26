package org.ppfc.api.model.service.user

import kotlinx.serialization.Serializable
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.teacher.TeacherResponse

@Serializable
data class UserResponse(
    val id: Long,
    val group: GroupResponse?,
    val teacher: TeacherResponse?,
    val isGroup: Boolean
)