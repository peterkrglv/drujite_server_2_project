package routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import requests.LoginRequest
import ru.drujite.requests.SignupRequest
import services.JwtService
import services.UserService

fun Route.signupRoute(jwtService: JwtService, userService: UserService) {
    post {
        val signupRequest = call.receive<SignupRequest>()

        val createdUser = userService.addUser(
            user = signupRequest.toModel()
        )?: return@post call.respond(HttpStatusCode.Conflict)
        val loginRequest = LoginRequest(signupRequest.phone, createdUser.password)
        val token: String? = jwtService.createJwtToken(loginRequest)
        token?.let {
            call.respond(hashMapOf("token" to token))
        } ?: call.respond(
            message = HttpStatusCode.Conflict
        )
    }
}
