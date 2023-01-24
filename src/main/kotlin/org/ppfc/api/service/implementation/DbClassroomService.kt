package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.ClassroomService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbClassroomService(private val database: Database) : ClassroomService {
    private val getOperationCache: Cache<Long, ClassroomResponse> = ExpiringCache(expiryTime = 3000L)

    override suspend fun add(classroom: ClassroomRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.classroomQueries.insertModel(classroom.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<ClassroomResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.classroomQueries.selectAll().executeAsList().map { classroomDto ->
                classroomDto.toResponse()
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<ClassroomResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                database.classroomQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
            }
        }
    }

    override suspend fun update(id: Long, classroom: ClassroomRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {
                database.classroomQueries.updateWhereId(
                    name = classroom.name,
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.classroomQueries.deleteWhereId(id = id)
        }
    }
}