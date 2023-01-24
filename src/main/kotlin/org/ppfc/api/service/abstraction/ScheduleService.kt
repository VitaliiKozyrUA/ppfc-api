package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.schedule.ScheduleResponse
import org.ppfc.api.service.ServiceResult

interface ScheduleService {
    suspend fun add(schedule: ScheduleRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<ScheduleResponse>>

    suspend fun get(id: Long): ServiceResult<ScheduleResponse?>

    suspend fun update(id: Long, schedule: ScheduleRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}