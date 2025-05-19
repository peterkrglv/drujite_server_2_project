package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.SessionModel
import requests.IdRequest
import requests.SessionRequest
import responses.IdResponse
import responses.SessionResponse
import services.JwtService
import services.SessionService

fun Route.sessionRoute(
    jwtService: JwtService,
    sessionService: SessionService,
) {
    authenticate {
        get("/user-sessions") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val sessions = sessionService.getSessionsByUserId(userId)
            call.respond(HttpStatusCode.OK, sessions.map { it.toResponse() })
        }

        get("/all") {
            val sessions = sessionService.getAllSessions()
            call.respond(HttpStatusCode.OK, sessions.map { it.toResponse() })
        }

        get {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val session = sessionService.getSession(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, session.toResponse())
        }

        post {
            val sessionRequest = call.receive<SessionRequest>()
            val id = sessionService.addSession(sessionRequest)
            call.respond(HttpStatusCode.Created, IdResponse(id = id))
        }

        delete {
            val request = call.receive<IdRequest>()
            sessionService.deleteSession(request.id)
            call.respond(HttpStatusCode.NoContent)
        }

    }
}


private fun SessionModel.toResponse(): SessionResponse {
    return SessionResponse(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        startDate = this.startDate,
        endDate = this.endDate,
        imageUrl = this.imageUrl
    )
}
