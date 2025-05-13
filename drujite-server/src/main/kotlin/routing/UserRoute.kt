package routing

import ru.drujite.requests.SignupRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.drujite.models.UserModel
import ru.drujite.responces.UserResponse
import services.JwtService
import services.UserService
import java.util.*

fun Route.userRoute(userService: UserService, jwtService: JwtService) {
    authenticate {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val foundUser = userService.findById(userId)
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respond(
                message = foundUser.toResponse()
            )
        }
    }
}

fun SignupRequest.toModel(): UserModel {
    return UserModel(
        id = UUID.randomUUID(),
        username = this.username,
        phone = this.phone,
        password = this.password,
        gender = this.gender
    )
}

private fun UserModel.toResponse(): UserResponse {
    return UserResponse(
        username = this.username,
        phone = this.phone
    )
}

private fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()