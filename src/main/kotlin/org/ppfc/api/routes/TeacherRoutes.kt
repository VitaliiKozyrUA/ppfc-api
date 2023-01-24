package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.teacherRouting() {
    val teacherService: TeacherService by inject()

    route("/teacher") {
        get {
            standardServiceResponseHandler(
                result = teacherService.getAll(),
                call = call
            ) { teachers ->
                var filteredTeachers: List<TeacherResponse> = teachers
                call.request.queryParameters["disciplineId"]?.toLongOrNull()?.let { disciplineId ->
                    filteredTeachers = teachers.filter { teacherResponse ->
                        teacherResponse.discipline.id == disciplineId
                    }
                }

                call.request.queryParameters["disciplineName"]?.let { disciplineName ->
                    filteredTeachers = teachers.filter { teacherResponse ->
                        teacherResponse.discipline.name == disciplineName
                    }
                }
                filteredTeachers
            }
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = teacherService.get(id),
                call = call
            )
        }

        post {
            val teacher = call.receive<TeacherRequest>()

            standardServiceResponseHandler(
                result = teacherService.add(teacher = teacher),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val teacher = call.receive<TeacherRequest>()

            standardServiceResponseHandler(
                result = teacherService.update(id = id, teacher = teacher),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = teacherService.delete(id = id),
                call = call
            )
        }
    }
}