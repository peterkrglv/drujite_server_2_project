package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.SessionModel
import requests.IdRequest
import responses.SessionResponse
import ru.drujite.requests.AddSessionByQRRequest
import services.JwtService
import services.UsersSessionsService

fun Route.usersSessionsRoute(
    jwtService: JwtService,
    usersSessionsService: UsersSessionsService,
) {
    authenticate {
        get {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val sessions = usersSessionsService.getUsersSessions(userId)
            call.respond(HttpStatusCode.OK, sessions.map { it.toResponse() })
        }

        post {
            val sessionRequest = call.receive<IdRequest>()
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@post call.respond(HttpStatusCode.Unauthorized)
            usersSessionsService.addUserSession(userId = userId, sessionId = sessionRequest.id)
            call.respond(HttpStatusCode.Created)
        }

        post("qr") {
            val request = call.receive<AddSessionByQRRequest>()
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val session = usersSessionsService.addUsersSessionByQr(userId, request.qr)
                if (session != null) {
                    call.respond(HttpStatusCode.Created, session.toResponse())
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
        }
    }
}

private fun SessionModel.toResponse(): SessionResponse = SessionResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    imageUrl = this.imageUrl
)

