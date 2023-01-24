package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.model.service.discipline.DisciplineResponse
import org.ppfc.api.service.ServiceResult

interface DisciplineService {
    suspend fun add(discipline: DisciplineRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<DisciplineResponse>>

    suspend fun get(id: Long): ServiceResult<DisciplineResponse?>

    suspend fun update(id: Long, discipline: DisciplineRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}