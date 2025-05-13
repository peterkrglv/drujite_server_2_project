package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import requests.AddUserSessionCharacter
import services.CharacterService
import services.JwtService
import services.UsersSessionsService

fun Route.usersCharactersRoute(
    jwtService: JwtService,
    usersSessionsService: UsersSessionsService,
    characterService: CharacterService
) {
    authenticate {
        get {
            val sessionId = call.request.queryParameters["sessionId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val character = usersSessionsService.getCharacter(
                userId = userId,
                sessionId = sessionId
            ) ?: return@get call.respond(HttpStatusCode.NotFound)
            val response = characterService.getCharacterWithClanAndUser(character.id) ?: return@get call.respond(
                HttpStatusCode.NotFound
            )
            call.respond(HttpStatusCode.OK, response)
        }

        post {
            val characterRequest = call.receive<AddUserSessionCharacter>()
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@post call.respond(HttpStatusCode.Unauthorized)
            usersSessionsService.addCharacter(
                userId = userId,
                sessionId = characterRequest.sessionId,
                characterId = characterRequest.characterId
            )
            call.respond(HttpStatusCode.Created)
        }

        get("/user-all") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val characters = usersSessionsService.getCharacters(userId)
            val response = characters.map { characterService.getCharacterWithClanAndUser(it.id) }
            call.respond(HttpStatusCode.OK, response)
        }

        get("session-all") {
            val id = call.request.queryParameters["sessionId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing 'id' parameter")
            val characters = usersSessionsService.getSessionsCharacters(id)
            val response = characters.map { characterService.getCharacterWithClanAndUser(it.id) }
            call.respond(HttpStatusCode.OK, response)
        }

        delete {
            val characterRequest = call.receive<AddUserSessionCharacter>()
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val result = usersSessionsService.deleteCharacter(
                userId = userId,
                sessionId = characterRequest.sessionId,
                characterId = characterRequest.characterId
            )
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
