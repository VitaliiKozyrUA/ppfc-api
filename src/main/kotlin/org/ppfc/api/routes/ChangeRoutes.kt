package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse
import org.ppfc.api.service.abstraction.ChangeService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.changeRouting() {
    val changeService: ChangeService by inject()

    route("/change") {
        get {
            standardServiceResponseHandler(
                result = changeService.getAll(),
                call = call
            ) { changes ->
                var filteredChanges: List<ChangeResponse> = changes

                call.request.queryParameters["date"]?.toLongOrNull()?.let { date ->
                    filteredChanges = changes.filter { changeResponse ->
                        changeResponse.dateUnix / 86_400_000 == date / 86_400_000
                    }
                }

                call.request.queryParameters["isNumerator"]?.toBooleanStrictOrNull()?.let { isNumerator ->
                    filteredChanges = changes.filter { changeResponse ->
                        changeResponse.isNumerator == isNumerator
                    }
                }

                call.request.queryParameters["groupId"]?.toLongOrNull()?.let { groupId ->
                    filteredChanges = changes.filter { changeResponse ->
                        changeResponse.group.id == groupId
                    }
                }

                call.request.queryParameters["groupNumber"]?.toLongOrNull()?.let { groupNumber ->
                    filteredChanges = changes.filter { changeResponse ->
                        changeResponse.group.number == groupNumber
                    }
                }

                call.request.queryParameters["teacherId"]?.toLongOrNull()?.let { teacherId ->
                    filteredChanges = changes.filter { changeResponse ->
                        changeResponse.teacher?.id == teacherId
                    }
                }

                filteredChanges
            }
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = changeService.get(id),
                call = call
            )
        }

        post {
            val change = call.receive<ChangeRequest>()

            standardServiceResponseHandler(
                result = changeService.add(change = change),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val change = call.receive<ChangeRequest>()

            standardServiceResponseHandler(
                result = changeService.update(id = id, change = change),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = changeService.delete(id = id),
                call = call
            )
        }
    }
}