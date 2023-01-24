package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.service.ServiceResult

interface GroupService {
    suspend fun add(group: GroupRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<GroupResponse>>

    suspend fun get(id: Long): ServiceResult<GroupResponse?>

    suspend fun update(id: Long, group: GroupRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}