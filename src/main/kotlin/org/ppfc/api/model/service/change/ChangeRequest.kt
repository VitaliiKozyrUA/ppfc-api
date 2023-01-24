package org.ppfc.api.model.service.change

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val groupId: Long,
    val classroomId: Long,
    val teacherId: Long?,
    val subjectId: Long?,
    val eventName: String?,
    val isSubject: Boolean,
    val lessonNumber: Long,
    val dateUnix: Long,
    val isNumerator: Boolean
)