package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.schedule.ScheduleResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.ServiceResult
import org.ppfc.api.service.abstraction.*
import org.ppfc.api.service.sqlServiceExceptionHandler

class DbScheduleService(private val database: Database) : ScheduleService, KoinComponent {
    private val groupService: GroupService by inject()
    private val classroomService: ClassroomService by inject()
    private val teacherService: TeacherService by inject()
    private val subjectService: SubjectService by inject()

    override suspend fun add(schedule: ScheduleRequest): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {

            if(schedule.subjectId == null && schedule.eventName == null) {
                throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
            }
            val isSubject = schedule.eventName == null

            database.scheduleQueries.insertModel(schedule.toDto(isSubject = isSubject))
        }
    }

    override suspend fun getAll(): ServiceResult<List<ScheduleResponse>> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.scheduleQueries.selectAll().executeAsList().mapNotNull { scheduleDto ->
                val groupResult = groupService.get(scheduleDto.groupId)
                val group = (groupResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                val classroomResult = classroomService.get(scheduleDto.classroomId)
                val classroom = (classroomResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                val teacherResult = teacherService.get(scheduleDto.teacherId)
                val teacher = (teacherResult as? ServiceResult.Success)?.data ?: return@mapNotNull null

                val subject = scheduleDto.subjectId?.let { subjectId ->
                    val subjectResult = subjectService.get(subjectId)
                    (subjectResult as? ServiceResult.Success)?.data
                }

                scheduleDto.toResponse(
                    group = group,
                    classroom = classroom,
                    teacher = teacher,
                    subject = subject
                )
            }
        }
    }

    override suspend fun get(id: Long): ServiceResult<ScheduleResponse?> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            val scheduleDto = database.scheduleQueries.selectWhereId(id = id).executeAsOneOrNull()
                ?: return@sqlServiceExceptionHandler null

            val groupResult = groupService.get(scheduleDto.groupId)
            val group = (groupResult as? ServiceResult.Success)?.data ?: return@sqlServiceExceptionHandler null

            val classroomResult = classroomService.get(scheduleDto.classroomId)
            val classroom = (classroomResult as? ServiceResult.Success)?.data ?: return@sqlServiceExceptionHandler null

            val teacherResult = teacherService.get(scheduleDto.teacherId)
            val teacher = (teacherResult as? ServiceResult.Success)?.data ?: return@sqlServiceExceptionHandler null

            val subject = scheduleDto.subjectId?.let { subjectId ->
                val subjectResult = subjectService.get(subjectId)
                (subjectResult as? ServiceResult.Success)?.data
            }

            scheduleDto.toResponse(
                group = group,
                classroom = classroom,
                teacher = teacher,
                subject = subject
            )
        }
    }

    override suspend fun update(id: Long, schedule: ScheduleRequest): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext sqlServiceExceptionHandler {

                if(schedule.subjectId == null && schedule.eventName == null) {
                    throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
                }
                val isSubject = schedule.eventName == null

                database.scheduleQueries.updateWhereId(
                    groupId = schedule.groupId,
                    classroomId = schedule.classroomId,
                    teacherId = schedule.teacherId,
                    subjectId = schedule.subjectId,
                    eventName = schedule.eventName,
                    isSubject = isSubject.toLong(),
                    lessonNumber = schedule.lessonNumber,
                    dayNumber = schedule.dayNumber,
                    isNumerator = schedule.isNumerator.toLong(),
                    id = id
                )
            }
        }

    override suspend fun delete(id: Long): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext sqlServiceExceptionHandler {
            database.scheduleQueries.deleteWhereId(id = id)
        }
    }
}