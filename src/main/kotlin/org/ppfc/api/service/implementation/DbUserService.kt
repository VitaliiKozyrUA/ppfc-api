package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.GroupService
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.abstraction.UserService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbUserService(private val database: Database) : UserService, KoinComponent {
    private val getOperationCache: Cache<String, UserResponse> = ExpiringCache(expiryTime = 3000L)
    private val groupService: GroupService by inject()
    private val teacherService: TeacherService by inject()

    override suspend fun add(user: UserRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.userQueries.insertModel(user.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<UserResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.userQueries.selectAll().executeAsList().mapNotNull { userDto ->
                val group = userDto.groupId?.let {
                    val groupResult = groupService.get(userDto.groupId)
                    (groupResult as? ServiceResult.Success)?.data ?: return@mapNotNull null
                }

                val teacher = userDto.teacherId?.let {
                    val teacherResult = teacherService.get(userDto.teacherId)
                    (teacherResult as? ServiceResult.Success)?.data ?: return@mapNotNull null
                }

                userDto.toResponse(group = group, teacher = teacher)
            }
        }
    }

    override suspend fun get(userCode: String): ServiceResult<UserResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(userCode) {
                val userDto = database.userQueries.selectWhereUserCode(userCode = userCode).executeAsOneOrNull()
                    ?: return@getValue null

                val group = userDto.groupId?.let {
                    val groupResult = groupService.get(userDto.groupId)
                    (groupResult as? ServiceResult.Success)?.data ?: return@getValue null
                }

                val teacher = userDto.teacherId?.let {
                    val teacherResult = teacherService.get(userDto.teacherId)
                    (teacherResult as? ServiceResult.Success)?.data ?: return@getValue null
                }

                userDto.toResponse(group = group, teacher = teacher)
            }
        }
    }

    override suspend fun update(userCode: String, user: UserRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {
                database.userQueries.updateWhereUserCode(
                    groupId = user.groupId,
                    teacherId = user.teacherId,
                    isGroup = user.isGroup.toLong(),
                    userCode = userCode
                )
            }
        }

    override suspend fun delete(userCode: String): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.userQueries.deleteWhereUserCode(userCode = userCode)
        }
    }
}