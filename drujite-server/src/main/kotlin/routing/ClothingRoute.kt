package ru.drujite.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import responses.IdResponse
import ru.drujite.requests.AddCharactersClothingRequest
import ru.drujite.services.ClothingService

fun Route.clothingRoute(
    clothingService: ClothingService
) {
    authenticate {
        post {
            val typeId = call.receive<IdResponse>().id
            val clothingId = clothingService.addClothingItem(typeId)
            call.respond(HttpStatusCode.Created, IdResponse(clothingId))
        }

        delete {
            val id = call.receive<IdResponse>().id
            val result = clothingService.deleteClothingItem(id)
            if (result) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Clothing item not found")
            }
        }

        get("/all") {
           val clothingItems = clothingService.getAllClothingItems()
              call.respond(HttpStatusCode.OK, clothingItems)
        }
    }
}