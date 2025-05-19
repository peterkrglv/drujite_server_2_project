package ru.drujite.db.mapping

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.drujite.requests.AddCharactersClothingRequest
import ru.drujite.services.ClothingService

fun Route.charactersClothingRoute(
    clothingService: ClothingService,
) {
    authenticate {
        post {
            val request = call.receive<AddCharactersClothingRequest>()
            val result = clothingService.addClothingItemsToCharacter(
                characterId = request.characterId,
                itemsIds = request.itemsIds
            )
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Character or clothing item not found")
            }
        }

        get {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val clothingItems = clothingService.getCharactersClothing(id)
                call.respond(HttpStatusCode.OK, clothingItems)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid character ID")
            }
        }
    }
}