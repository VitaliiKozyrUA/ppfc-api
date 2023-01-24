package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.service.abstraction.CourseService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.courseRouting() {
    val courseService: CourseService by inject()

    route("/course") {
        get {
            standardServiceResponseHandler(
                result = courseService.getAll(),
                call = call
            )
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = courseService.get(id),
                call = call
            )
        }

        post {
            val course = call.receive<CourseRequest>()

            standardServiceResponseHandler(
                result = courseService.add(course = course),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val course = call.receive<CourseRequest>()

            standardServiceResponseHandler(
                result = courseService.update(id = id, course = course),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = courseService.delete(id = id),
                call = call
            )
        }
    }
}