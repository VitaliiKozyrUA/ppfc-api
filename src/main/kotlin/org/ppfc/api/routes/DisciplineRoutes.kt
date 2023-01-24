package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.service.abstraction.DisciplineService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.disciplineRouting() {
    val disciplineService: DisciplineService by inject()

    route("/discipline") {
        get {
            standardServiceResponseHandler(
                result = disciplineService.getAll(),
                call = call
            )
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = disciplineService.get(id),
                call = call
            )
        }

        post {
            val discipline = call.receive<DisciplineRequest>()

            standardServiceResponseHandler(
                result = disciplineService.add(discipline = discipline),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val discipline = call.receive<DisciplineRequest>()

            standardServiceResponseHandler(
                result = disciplineService.update(id = id, discipline = discipline),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = disciplineService.delete(id = id),
                call = call
            )
        }
    }
}