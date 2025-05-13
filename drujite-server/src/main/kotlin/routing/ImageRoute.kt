package ru.drujite.routing

import db.repos_impls.EventRepositoryImpl
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.drujite.services.ImageService

fun Route.imageRoute(imageService: ImageService) {
    val logger: Logger = LoggerFactory.getLogger(Route::class.java)
    authenticate {
        post("/{entityType}/{id}") {
            logger.info("POST img")
            val entityType = call.parameters["entityType"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Entity type is required"
            )
            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Invalid ID"
            )
            logger.info("Entity type: $entityType, ID: $id")

            val multipart = call.receiveMultipart()
            logger.info("Received multipart data")
            var tempFileBytes: ByteArray? = null
            var fileExtension: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    fileExtension = part.originalFileName?.substringAfterLast('.', "jpg")
                    tempFileBytes = part.streamProvider().readBytes()
                }
                part.dispose()
            }
            logger.info("File extension: $fileExtension, File bytes size: ${tempFileBytes?.size}")

            val fileBytes = tempFileBytes // Локальная переменная для безопасного использования
            if (fileBytes != null) {
                logger.info("File bytes are not null")
                val success = imageService.saveImage(entityType, id, fileBytes, fileExtension ?: "jpg")
                if (success) {
                    call.respond(HttpStatusCode.OK, "Image uploaded successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save image")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid file upload")
            }
        }

        post("/{entityType}/{id}/binary") {
            val entityType = call.parameters["entityType"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Entity type is required"
            )
            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Invalid ID"
            )

            val fileBytes = call.receive<ByteArray>()
            val success = imageService.saveImage(entityType, id, fileBytes, "jpg")
            if (success) {
                call.respond(HttpStatusCode.OK, "Image uploaded successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to save image")
            }
        }
    }

    get("/{entityType}/{id}") {
        val entityType = call.parameters["entityType"] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            "Entity type is required"
        )
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            "Invalid ID"
        )

        val file = imageService.getImagePath(entityType, id)
        if (file != null && file.exists()) {
            call.respondFile(file)
        } else {
            call.respond(HttpStatusCode.NotFound, "File not found")
        }
    }
}