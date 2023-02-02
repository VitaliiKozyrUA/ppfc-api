package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.schedule.ScheduleResponse
import org.ppfc.api.service.abstraction.ScheduleService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.scheduleRouting() {
    val scheduleService: ScheduleService by inject()

    route("/schedule") {
        get {
            standardServiceResponseHandler(
                result = scheduleService.getAll(),
                call = call
            ) { schedule ->
                var filteredSchedule: List<ScheduleResponse> = schedule

                call.request.queryParameters["dayNumber"]?.toLongOrNull()?.let { dayNumber ->
                    filteredSchedule = filteredSchedule.filter { scheduleResponse ->
                        scheduleResponse.dayNumber == dayNumber
                    }
                }

                call.request.queryParameters["isNumerator"]?.toBooleanStrictOrNull()?.let { isNumerator ->
                    filteredSchedule = filteredSchedule.filter { scheduleResponse ->
                        scheduleResponse.isNumerator == isNumerator
                    }
                }

                call.request.queryParameters["groupId"]?.toLongOrNull()?.let { groupId ->
                    filteredSchedule = filteredSchedule.filter { scheduleResponse ->
                        scheduleResponse.group.id == groupId
                    }
                }

                call.request.queryParameters["groupNumber"]?.toLongOrNull()?.let { groupNumber ->
                    filteredSchedule = filteredSchedule.filter { scheduleResponse ->
                        scheduleResponse.group.number == groupNumber
                    }
                }

                call.request.queryParameters["teacherId"]?.toLongOrNull()?.let { teacherId ->
                    filteredSchedule = filteredSchedule.filter { scheduleResponse ->
                        scheduleResponse.teacher.id == teacherId
                    }
                }

                filteredSchedule
            }
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = scheduleService.get(id),
                call = call
            )
        }

        post {
            val schedule = call.receive<ScheduleRequest>()

            standardServiceResponseHandler(
                result = scheduleService.add(schedule = schedule),
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val schedule = call.receive<ScheduleRequest>()

            standardServiceResponseHandler(
                result = scheduleService.update(id = id, schedule = schedule),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = scheduleService.delete(id = id),
                call = call
            )
        }
    }
}