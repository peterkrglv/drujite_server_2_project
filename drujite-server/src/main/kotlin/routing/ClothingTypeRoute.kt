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
            call.respond(HttpStatusCode.OK, clothingTypes.map { ClothingTypeResponse(it.id, it.name, it.isEditable) })
        }

        post {
            val request = call.receive<ClothingTypeRequest>()
            val id = clothingService.addClothingType(request.toModel())
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

private fun ClothingTypeRequest.toModel() = ClothingType(
    id = 0,
    name = name,
    isEditable = isEditable
)

private fun ClothingType.toResponse() = ClothingTypeResponse(
    id = id,
    name = name,
    isEditable = isEditable
)
