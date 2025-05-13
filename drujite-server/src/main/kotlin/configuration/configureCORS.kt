package ru.drujite.configuration

import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.application.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
        // Для продакшена указать конкретные хосты:
        // allowHost("example.com", schemes = listOf("http", "https"))
    }
}