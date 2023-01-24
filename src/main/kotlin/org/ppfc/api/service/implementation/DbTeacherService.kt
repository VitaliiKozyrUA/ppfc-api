package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.cache.Cache
import org.ppfc.api.common.cache.ExpiringCache
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.DisciplineService
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbTeacherService(private val database: Database) : TeacherService, KoinComponent {
    private val getOperationCache: Cache<Long, TeacherResponse> = ExpiringCache(expiryTime = 3000L)
    private val disciplineService: DisciplineService by inject()

    override suspend fun add(teacher: TeacherRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.teacherQueries.insertModel(teacher.toDto())
        }
    }

    override suspend fun getAll(): ServiceResult<List<TeacherResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.teacherQueries.selectAll().executeAsList().mapNotNull { teacherDto ->
                val disciplineResult = disciplineService.get(teacherDto.disciplineId)
                val discipline = (disciplineResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                teacherDto.toResponse(discipline = discipline)
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<TeacherResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            getOperationCache.getValue(id) {
                val teacher = database.teacherQueries.selectWhereId(id = id).executeAsOneOrNull()
                    ?: return@getValue null

                val disciplineResult = disciplineService.get(teacher.disciplineId)
                val discipline =
                    (disciplineResult as? ServiceResult.Success)?.data ?: return@getValue null

                teacher.toResponse(discipline = discipline)
            }
        }
    }

    override suspend fun update(id: Long, teacher: TeacherRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.teacherQueries.updateWhereId(
                firstName = teacher.firstName,
                middleName = teacher.middleName,
                lastName = teacher.lastName,
                disciplineId = teacher.disciplineId,
                isHeadTeacher = teacher.isHeadTeacher.toLong(),
                id = id
            )
        }
    }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.teacherQueries.deleteWhereId(id = id)
        }
    }
}