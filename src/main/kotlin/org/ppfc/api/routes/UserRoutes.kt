package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse
import org.ppfc.api.service.abstraction.UserService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.userRouting() {
    val userService: UserService by inject()

    route("/user") {
        get {
            standardServiceResponseHandler(
                result = userService.getAll(),
                call = call
            ) { users ->
                var filteredUsers: List<UserResponse> = users
                call.request.queryParameters["isStudent"]?.toBooleanStrictOrNull()?.let { isGroup ->
                    filteredUsers = users.filter { userResponse ->
                        userResponse.isGroup == isGroup
                    }
                }

                filteredUsers
            }
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = userService.get(id = id),
                call = call
            )
        }

        post {
            val user = call.receive<UserRequest>()

            standardServiceResponseHandler(
                result = userService.add(user = user),
                call = call
            )
        }

        put {
            val user = call.receive<UserRequest>()

            standardServiceResponseHandler(
                result = userService.update(user = user),
                call = call
            )
        }

        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@delete
            }

            standardServiceResponseHandler(
                result = userService.delete(id = id),
                call = call
            )
        }
    }
}