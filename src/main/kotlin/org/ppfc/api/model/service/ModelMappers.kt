package org.ppfc.api.model.service

import org.ppfc.api.common.toBoolean
import org.ppfc.api.common.toLong
import org.ppfc.api.database.*
import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse
import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.model.service.course.CourseResponse
import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.model.service.discipline.DisciplineResponse
import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.schedule.ScheduleResponse
import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse

fun ClassroomDto.toResponse() = ClassroomResponse(
    id = this.id,
    name = this.name
)

fun ClassroomRequest.toDto() = ClassroomDto(
    id = 0L,
    name = this.name
)

fun CourseDto.toResponse() = CourseResponse(
    id = this.id,
    number = this.number
)

fun CourseRequest.toDto() = CourseDto(
    id = 0L,
    number = this.number
)

fun DisciplineDto.toResponse() = DisciplineResponse(
    id = this.id,
    name = this.name
)

fun DisciplineRequest.toDto() = DisciplineDto(
    id = 0L,
    name = this.name
)

fun SubjectDto.toResponse() = SubjectResponse(
    id = this.id,
    name = this.name
)

fun SubjectRequest.toDto() = SubjectDto(
    id = 0L,
    name = this.name
)

fun GroupDto.toResponse(course: CourseResponse) = GroupResponse(
    id = this.id,
    number = this.number,
    course = course
)

fun GroupRequest.toDto() = GroupDto(
    id = 0L,
    number = this.number,
    courseId = this.courseId
)

fun TeacherDto.toResponse(discipline: DisciplineResponse) = TeacherResponse(
    id = this.id,
    firstName = this.firstName,
    middleName = this.middleName,
    lastName = this.lastName,
    discipline = discipline,
    isHeadTeacher = this.isHeadTeacher.toBoolean()
)

fun TeacherRequest.toDto() = TeacherDto(
    id = 0L,
    firstName = this.firstName,
    middleName = this.middleName,
    lastName = this.lastName,
    disciplineId = this.disciplineId,
    isHeadTeacher = this.isHeadTeacher.toLong()
)

fun ScheduleDto.toResponse(
    group: GroupResponse,
    classroom: ClassroomResponse,
    teacher: TeacherResponse,
    subject: SubjectResponse?
) = ScheduleResponse(
    id = this.id,
    group = group,
    classroom = classroom,
    teacher = teacher,
    subject = subject,
    eventName = this.eventName,
    isSubject = this.isSubject.toBoolean(),
    lessonNumber = this.lessonNumber,
    dayNumber = this.dayNumber,
    isNumerator = this.isNumerator.toBoolean()
)

fun ScheduleRequest.toDto(isSubject: Boolean) = ScheduleDto(
    id = 0L,
    groupId = this.groupId,
    classroomId = this.classroomId,
    teacherId = this.teacherId,
    subjectId = this.subjectId,
    eventName = this.eventName,
    isSubject = isSubject.toLong(),
    lessonNumber = this.lessonNumber,
    dayNumber = this.dayNumber,
    isNumerator = this.isNumerator.toLong()
)

fun ChangeDto.toResponse(
    group: GroupResponse,
    classroom: ClassroomResponse,
    teacher: TeacherResponse?,
    subject: SubjectResponse?
) = ChangeResponse(
    id = this.id,
    group = group,
    classroom = classroom,
    teacher = teacher,
    subject = subject,
    eventName = this.eventName,
    isSubject = this.isSubject.toBoolean(),
    lessonNumber = this.lessonNumber,
    date = this.date,
    isNumerator = this.isNumerator.toBoolean()
)

fun ChangeRequest.toDto(isSubject: Boolean) = ChangeDto(
    id = 0L,
    groupId = this.groupId,
    classroomId = this.classroomId,
    teacherId = this.teacherId,
    subjectId = this.subjectId,
    eventName = this.eventName,
    isSubject = isSubject.toLong(),
    lessonNumber = this.lessonNumber,
    date = this.date,
    isNumerator = this.isNumerator.toLong()
)

fun UserDto.toResponse(group: GroupResponse?, teacher: TeacherResponse?) = UserResponse(
    id = this.id,
    group = group,
    teacher = teacher,
    isGroup = this.isGroup.toBoolean()
)

fun UserRequest.toDto(isGroup: Boolean) = UserDto(
    id = this.id,
    groupId = this.groupId,
    teacherId = this.teacherId,
    isGroup = isGroup.toLong()
)