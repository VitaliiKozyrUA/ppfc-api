package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.service.abstraction.GroupService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.groupRouting() {
    val groupService: GroupService by inject()

    route("/group") {
        get {
            standardServiceResponseHandler(
                result = groupService.getAll(),
                call = call
            ) { groups ->
                var filteredGroups: List<GroupResponse> = groups

                call.request.queryParameters["courseId"]?.toLongOrNull()?.let { courseId ->
                    filteredGroups = groups.filter { groupResponse ->
                        groupResponse.course.id == courseId
                    }
                }

                call.request.queryParameters["courseNumber"]?.toLongOrNull()?.let { courseNumber ->
                    filteredGroups = groups.filter { groupResponse ->
                        groupResponse.course.number == courseNumber
                    }
                }

                filteredGroups
            }
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = groupService.get(id),
                call = call
            )
        }

        post {
            val group = call.receive<GroupRequest>()

            standardServiceResponseHandler(
                result = groupService.add(group = group),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val group = call.receive<GroupRequest>()

            standardServiceResponseHandler(
                result = groupService.update(id = id, group = group),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = groupService.delete(id = id),
                call = call
            )
        }
    }
}