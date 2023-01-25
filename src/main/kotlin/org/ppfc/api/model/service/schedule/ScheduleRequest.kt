package org.ppfc.api.model.service.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRequest(
    val groupId: Long,
    val classroomId: Long,
    val teacherId: Long,
    val subjectId: Long?,
    val eventName: String?,
    val lessonNumber: Long,
    val dayNumber: Long,
    val isNumerator: Boolean
)