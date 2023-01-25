package org.ppfc.api.model.service.change

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val groupId: Long,
    val classroomId: Long,
    val teacherId: Long?,
    val subjectId: Long?,
    val eventName: String?,
    val lessonNumber: Long,
    val date: String,
    val isNumerator: Boolean
)