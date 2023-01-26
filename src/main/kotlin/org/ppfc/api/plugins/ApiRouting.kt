package org.ppfc.api.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.ppfc.api.routes.*

fun Application.configureApiRouting() {
    routing {
        resource("/api", "api-doc.html")

        route("/api") {
            authRouting()

            authenticate {
                classroomRouting()
                courseRouting()
                disciplineRouting()
                teacherRouting()
                subjectRouting()
                groupRouting()
                scheduleRouting()
                changeRouting()
                userRouting()
            }
        }
    }
}
