package org.ppfc.api.model.service.teacher

import kotlinx.serialization.Serializable
import org.ppfc.api.model.service.discipline.DisciplineResponse

@Serializable
data class TeacherResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val discipline: DisciplineResponse,
    val isHeadTeacher: Boolean
)