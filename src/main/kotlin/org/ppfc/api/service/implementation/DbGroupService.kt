package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.CourseService
import org.ppfc.api.service.abstraction.GroupService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbGroupService(private val database: Database) : GroupService, KoinComponent {
    private val getOperationCache: Cache<Long, GroupResponse> = ExpiringCache(expiryTime = 3000L)
    private val courseService: CourseService by inject()

    override suspend fun add(group: GroupRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.groupQueries.insertModel(group.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<GroupResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.groupQueries.selectAll().executeAsList().mapNotNull { groupDto ->
                val courseResult = courseService.get(groupDto.courseId)
                val course = (courseResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                groupDto.toResponse(course = course)
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<GroupResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                val group = database.groupQueries.selectWhereId(id = id).executeAsOneOrNull()
                    ?: return@getValue null

                val courseResult = courseService.get(group.courseId)
                val course = (courseResult as? ServiceResult.Success)?.data ?: return@getValue null

                group.toResponse(course = course)
            }
        }
    }

    override suspend fun update(id: Long, group: GroupRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.groupQueries.updateWhereId(
                number = group.number, courseId = group.courseId, id = id
            )
        }
    }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.groupQueries.deleteWhereId(id = id)
        }
    }
}