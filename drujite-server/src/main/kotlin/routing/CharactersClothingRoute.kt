package ru.drujite.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.drujite.requests.AddCharactersClothingRequest
import ru.drujite.services.ClothingService

fun Route.charactersClothingRoute(
    clothingService: ClothingService
) {
    authenticate {
        get {
            val characterId = call.request.queryParameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val clothingItems = clothingService.getCharactersClothing(characterId)
            call.respond(HttpStatusCode.OK, clothingItems)
        }

        get("editable") {
            val characterId = call.request.queryParameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val clothingItems = clothingService.getCharactersEditableClothingItems(characterId)
            call.respond(HttpStatusCode.OK, clothingItems)
        }

        post {
            val request = call.receive<AddCharactersClothingRequest>()
            val characterId = request.characterId
            val itemsIds = request.itemsIds
            val result = clothingService.addClothingItemsToCharacter(characterId, itemsIds)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}