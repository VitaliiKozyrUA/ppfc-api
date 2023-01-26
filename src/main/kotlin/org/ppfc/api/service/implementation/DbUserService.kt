package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.GroupService
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.abstraction.UserService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbUserService(private val database: Database) : UserService, KoinComponent {
    private val getOperationCache: Cache<Long, UserResponse> = ExpiringCache(expiryTime = 3000L)
    private val groupService: GroupService by inject()
    private val teacherService: TeacherService by inject()

    override suspend fun add(user: UserRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {

            if(user.groupId == null && user.teacherId == null) {
                throw MalformedModelException(message = StringResource.fieldsGroupIdAndTeacherIdAreNull)
            }
            val isGroup = user.teacherId == null

            database.userQueries.insertModel(user.toDto(isGroup = isGroup))
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

    override suspend fun get(id: Long): ServiceResult<UserResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                val userDto = database.userQueries.selectWhereUserCode(id = id).executeAsOneOrNull()
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

    override suspend fun update(user: UserRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {

                if(user.groupId == null && user.teacherId == null) {
                    throw MalformedModelException(message = StringResource.fieldsGroupIdAndTeacherIdAreNull)
                }
                val isGroup = user.teacherId == null

                database.userQueries.updateWhereUserCode(
                    groupId = user.groupId,
                    teacherId = user.teacherId,
                    isGroup = isGroup.toLong(),
                    id = user.id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.userQueries.deleteWhereUserCode(id = id)
        }
    }
}