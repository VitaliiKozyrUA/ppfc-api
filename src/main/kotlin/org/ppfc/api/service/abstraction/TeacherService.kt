package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.service.ServiceResult

interface TeacherService {
    suspend fun add(teacher: TeacherRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<TeacherResponse>>

    suspend fun get(id: Long): ServiceResult<TeacherResponse?>

    suspend fun update(id: Long, teacher: TeacherRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}