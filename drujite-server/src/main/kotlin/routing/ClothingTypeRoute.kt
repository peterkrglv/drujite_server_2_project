package ru.drujite.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import responses.IdResponse
import ru.drujite.models.ClothingType
import ru.drujite.requests.ClothingTypeRequest
import ru.drujite.responses.ClothingTypeResponse
import ru.drujite.services.ClothingService

fun Route.clothingTypeRoute(
    clothingService: ClothingService
) {
    authenticate {
        get("/all") {
            val clothingTypes = clothingService.getClothingTypes()
            call.respond(HttpStatusCode.OK, clothingTypes.map { ClothingTypeResponse(it.id, it.name) })
        }

        post {
            val name = call.receive<ClothingTypeRequest>().name
            val id = clothingService.addClothingType(name)
            call.respond(HttpStatusCode.Created, IdResponse(id))
        }

        delete {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest
            )
            val result = clothingService.deleteClothingType(id)
            if (result) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Clothing type not found")
            }
        }
    }
}

private fun ClothingType.toResponse() = ClothingTypeResponse(
    id = id,
    name = name
)
