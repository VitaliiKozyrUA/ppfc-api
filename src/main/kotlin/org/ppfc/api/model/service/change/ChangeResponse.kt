package org.ppfc.api.model.service.change

import kotlinx.serialization.Serializable
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.teacher.TeacherResponse

@Serializable
data class ChangeResponse(
    val id: Long,
    val group: GroupResponse,
    val classroom: ClassroomResponse,
    val teacher: TeacherResponse?,
    val subject: SubjectResponse?,
    val eventName: String?,
    val isSubject: Boolean,
    val lessonNumber: Long,
    val date: String,
    val isNumerator: Boolean
)