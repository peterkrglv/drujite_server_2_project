package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.ClanModel
import requests.AddClanRequest
import requests.AddClanToSessionRequest
import requests.IdRequest
import responses.ClanResponse
import responses.IdResponse
import services.ClanService

fun Route.clanRoute(
    clanService: ClanService
) {
    authenticate {
        get() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val clan = clanService.getClan(id)
            if (clan == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(HttpStatusCode.OK, clan.toResponse())
            }
        }

        post() {
            val request = call.receive<AddClanRequest>()
            val clan = request.toModel()
            val clanId = clanService.addClan(clan)
            call.respond(HttpStatusCode.Created, IdResponse(clanId))
        }

        delete() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest
            )
            val result = clanService.deleteClan(id)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/add-to-session") {
            val request = call.receive<AddClanToSessionRequest>()
            val result = clanService.addClanToSession(request.clanId, request.sessionId)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/session-all") {
            val id = call.request.queryParameters["sessionId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val clans = clanService.getSessionsClans(id)
            call.respond(HttpStatusCode.OK, clans.map { it.toResponse() })
        }

        get("all") {
            val clans = clanService.getAllClans()
            call.respond(HttpStatusCode.OK, clans.map { it.toResponse() })
        }
    }
}

private fun AddClanRequest.toModel() =
    ClanModel(
        id = 0,
        name = this.name,
        description = this.description
    )

private fun ClanModel.toResponse() =
    ClanResponse(
        id = this.id,
        name = this.name,
        description = this.description
    )