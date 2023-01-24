package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.service.ServiceResult

interface ClassroomService {
    suspend fun add(classroom: ClassroomRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<ClassroomResponse>>

    suspend fun get(id: Long): ServiceResult<ClassroomResponse?>

    suspend fun update(id: Long, classroom: ClassroomRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}