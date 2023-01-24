package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.model.service.discipline.DisciplineResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.DisciplineService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbDisciplineService(private val database: Database) : DisciplineService {
    private val getOperationCache: Cache<Long, DisciplineResponse> = ExpiringCache(expiryTime = 3000L)

    override suspend fun add(discipline: DisciplineRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.disciplineQueries.insertModel(discipline.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<DisciplineResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.disciplineQueries.selectAll().executeAsList().map { disciplineDto ->
                disciplineDto.toResponse()
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<DisciplineResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                database.disciplineQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
            }
        }
    }

    override suspend fun update(id: Long, discipline: DisciplineRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {
                database.disciplineQueries.updateWhereId(
                    name = discipline.name,
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.disciplineQueries.deleteWhereId(id = id)
        }
    }
}