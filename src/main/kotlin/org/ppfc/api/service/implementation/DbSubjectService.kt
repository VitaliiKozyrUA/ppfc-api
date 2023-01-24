package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.SubjectService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbSubjectService(private val database: Database) : SubjectService {
    private val getOperationCache: Cache<Long, SubjectResponse> = ExpiringCache(expiryTime = 3000L)

    override suspend fun add(subject: SubjectRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.subjectQueries.insertModel(subject.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<SubjectResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.subjectQueries.selectAll().executeAsList().map { subjectDto ->
                subjectDto.toResponse()
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<SubjectResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                database.subjectQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
            }
        }
    }

    override suspend fun update(id: Long, subject: SubjectRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {
                database.subjectQueries.updateWhereId(
                    name = subject.name,
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.subjectQueries.deleteWhereId(id = id)
        }
    }
}