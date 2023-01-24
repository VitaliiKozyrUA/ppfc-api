package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.model.service.course.CourseResponse
import org.ppfc.api.service.ServiceResult

interface CourseService {
    suspend fun add(course: CourseRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<CourseResponse>>

    suspend fun get(id: Long): ServiceResult<CourseResponse?>

    suspend fun update(id: Long, course: CourseRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}