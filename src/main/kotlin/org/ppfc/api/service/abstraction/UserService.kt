package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse
import org.ppfc.api.service.ServiceResult

interface UserService {
    suspend fun add(user: UserRequest): ServiceResult<Unit>

    suspend fun getAll(): ServiceResult<List<UserResponse>>

    suspend fun get(id: Long): ServiceResult<UserResponse?>

    suspend fun update(user: UserRequest): ServiceResult<Unit>

    suspend fun delete(id: Long): ServiceResult<Unit>
}