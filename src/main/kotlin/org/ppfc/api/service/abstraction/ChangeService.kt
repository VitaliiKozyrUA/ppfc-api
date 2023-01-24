package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse
import org.ppfc.api.service.ServiceResult

interface ChangeService {
    suspend fun add(change: ChangeRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<ChangeResponse>>

    suspend fun get(id: Long): ServiceResult<ChangeResponse?>

    suspend fun update(id: Long, change: ChangeRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}