package org.ppfc.api.service

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.ppfc.api.common.StringResource

suspend inline fun <reified T> standardServiceResponseHandler(
    result: ServiceResult<T>,
    call: ApplicationCall,
    onSuccess: ((T) -> T) = { it }
) {
    when (result) {
        is ServiceResult.Success -> {
            result.data?.let { data ->
                call.respond(
                    status = HttpStatusCode.OK,
                    message = onSuccess.invoke(data) ?: data
                )
            } ?: call.respond(
                status = HttpStatusCode.NotFound,
                message = StringResource.resourceNotFound
            )
        }

        is ServiceResult.Failure -> {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = result.message
            )
        }
    }
}