package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.EventModel
import requests.AddEventRequest
import requests.IdRequest
import requests.GetTimetableBySessionAndDate
import responses.EventResponse
import responses.IdResponse
import services.TimeTableService

fun Route.eventRoute(
    timeTableService: TimeTableService
) {
    authenticate {
        post() {
            val request = call.receive<AddEventRequest>()
            val event = request.toModel()
            val eventId = timeTableService.addEvent(event)
            call.application.environment.log.info("Event $eventId added")
            if (eventId != null) {
                call.respond(HttpStatusCode.OK, IdResponse(eventId))
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest
            )
            val result = timeTableService.deleteEvent(id)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val event = timeTableService.getEvent(id)
            if (event != null) {
                call.respond(HttpStatusCode.OK, event.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/session-date") {
            val request = call.receive<GetTimetableBySessionAndDate>()
            val events = timeTableService.getEventsBySessionAndDate(request.sessionId, request.date)
            if (events.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, events.map { it.toResponse() })
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("by-timetable") {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val events = timeTableService.getEventsByTimetableId(id)
            if (events.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, events.map { it.toResponse() })
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun EventModel.toResponse() = EventResponse(
    id = this.id,
    timetableId = this.timetableId,
    num = this.num,
    name = this.name,
    time = this.time,
    isTitle = this.isTitle
)

private fun AddEventRequest.toModel() =
    EventModel(
        id = 0,
        timetableId = this.timetableId,
        num = this.num,
        name = this.name,
        time = this.time,
        isTitle = this.isTitle
    )

