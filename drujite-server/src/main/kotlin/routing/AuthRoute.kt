package ru.drujite.routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import requests.LoginRequest
import services.JwtService

fun Route.authRoute(jwtService: JwtService) {
    post {
        val user = call.receive<LoginRequest>()
        val token: String? = jwtService.createJwtToken(user)
        token?.let {
            call.respond(hashMapOf("token" to token))
        } ?: call.respond(
            message = HttpStatusCode.Unauthorized
        )
    }
}