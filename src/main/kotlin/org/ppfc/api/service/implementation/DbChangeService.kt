package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.*
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbChangeService(private val database: Database) : ChangeService, KoinComponent {
    private val groupService: GroupService by inject()
    private val classroomService: ClassroomService by inject()
    private val teacherService: TeacherService by inject()
    private val subjectService: SubjectService by inject()

    override suspend fun add(change: ChangeRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {

            if(change.subjectId == null && change.eventName == null) {
                throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
            }
            val isSubject = change.eventName == null

            database.changeQueries.insertModel(change.toDto(isSubject = isSubject))
        }
    }

    override suspend fun getAll(): ServiceResult<List<ChangeResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.changeQueries.selectAll().executeAsList().mapNotNull { changeDto ->
                val groupResult = groupService.get(changeDto.groupId)
                val group = (groupResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                val classroomResult = classroomService.get(changeDto.classroomId)
                val classroom = (classroomResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                val teacher = changeDto.teacherId?.let { teacherId ->
                    val teacherResult = teacherService.get(teacherId)
                    (teacherResult as? ServiceResult.Success)?.data
                }

                val subject = changeDto.subjectId?.let { subjectId ->
                    val subjectResult = subjectService.get(subjectId)
                    (subjectResult as? ServiceResult.Success)?.data
                }

                changeDto.toResponse(
                    group = group,
                    classroom = classroom,
                    teacher = teacher,
                    subject = subject
                )
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<ChangeResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            val changeDto = database.changeQueries.selectWhereId(id = id).executeAsOneOrNull()
                ?: return@sqlServiceExceptionHandler null

            val groupResult = groupService.get(changeDto.groupId)
            val group = (groupResult as? ServiceResult.Success)?.data ?: return@sqlServiceExceptionHandler null

            val classroomResult = classroomService.get(changeDto.classroomId)
            val classroom = (classroomResult as? ServiceResult.Success)?.data ?: return@sqlServiceExceptionHandler null

            val teacher = changeDto.teacherId?.let { teacherId ->
                val teacherResult = teacherService.get(teacherId)
                (teacherResult as? ServiceResult.Success)?.data
            }

            val subject = changeDto.subjectId?.let { subjectId ->
                val subjectResult = subjectService.get(subjectId)
                (subjectResult as? ServiceResult.Success)?.data
            }

            changeDto.toResponse(
                group = group,
                classroom = classroom,
                teacher = teacher,
                subject = subject
            )
        }
    }

    override suspend fun update(id: Long, change: ChangeRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {

                if(change.subjectId == null && change.eventName == null) {
                    throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
                }
                val isSubject = change.eventName == null

                database.changeQueries.updateWhereId(
                    groupId = change.groupId,
                    classroomId = change.classroomId,
                    teacherId = change.teacherId,
                    subjectId = change.subjectId,
                    eventName = change.eventName,
                    isSubject = isSubject.toLong(),
                    lessonNumber = change.lessonNumber,
                    date = change.date,
                    isNumerator = change.isNumerator.toLong(),
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.changeQueries.deleteWhereId(id = id)
        }
    }
}