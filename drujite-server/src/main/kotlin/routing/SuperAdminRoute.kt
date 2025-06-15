package ru.drujite.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import models.UserModel
import ru.drujite.requests.MakeAdminRequest
import ru.drujite.responses.UserAdminResponse
import services.JwtService
import services.UserService

fun Route.superAdminRoute(
    jwtService: JwtService,
    userService: UserService
) {
    authenticate {
        post("make-admin") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@post call.respond(HttpStatusCode.Unauthorized)
            if (!userService.checkIfSuperAdmin(userId)) {
                return@post call.respond(HttpStatusCode.Forbidden)
            }
            val request = call.receive<MakeAdminRequest>()
            val user = userService.findByPhone(request.phone)
                ?: return@post call.respond(HttpStatusCode.NotFound)
            if (userService.makeAdmin(user.id)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        get("get-users") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            if (!userService.checkIfSuperAdmin(userId)) {
                return@get call.respond(HttpStatusCode.Forbidden)
            }
            val users = userService.getAllUsers()
            call.respond(users.map { it.toResponse() })
        }
    }
}

private fun UserModel.toResponse(): UserAdminResponse {
    return UserAdminResponse(
        phone = phone,
        username = username,
        isAdmin = isAdmin,
    )
}
