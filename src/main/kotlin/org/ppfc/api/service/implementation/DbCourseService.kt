package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.model.service.course.CourseResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.CourseService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbCourseService(private val database: Database) : CourseService {
    private val getOperationCache: Cache<Long, CourseResponse> = ExpiringCache(expiryTime = 3000L)

    override suspend fun add(course: CourseRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.courseQueries.insertModel(course.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<CourseResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.courseQueries.selectAll().executeAsList().map { courseDto ->
                courseDto.toResponse()
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<CourseResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                database.courseQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
            }
        }
    }

    override suspend fun update(id: Long, course: CourseRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {
                database.courseQueries.updateWhereId(
                    name = course.number,
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.courseQueries.deleteWhereId(id = id)
        }
    }
}