package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.service.ServiceResult

interface SubjectService {
    suspend fun add(subject: SubjectRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<SubjectResponse>>

    suspend fun get(id: Long): ServiceResult<SubjectResponse?>

    suspend fun update(id: Long, subject: SubjectRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}