package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.GoalModel
import requests.AddGoalRequest
import requests.IdRequest
import responses.GoalResponse
import responses.IdResponse
import services.GoalService
import services.JwtService


fun Route.goalRoute(
    goalService: GoalService,
    jwtService: JwtService,
) {
    authenticate {
        get() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val goal = goalService.getGoal(id)
            if (goal != null) {
                call.respond(goal.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post() {
            val request = call.receive<AddGoalRequest>()
            val goalId = goalService.addGoal(request)?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.Created, IdResponse(goalId))
        }

        delete() {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest
            )
            val result = goalService.deleteGoal(id)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/character-all") {
            val principal = call.principal<JWTPrincipal>()
            val userId =
                principal?.let { jwtService.extractId(it) } ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val sessionId = call.request.queryParameters["sessionId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val goals = goalService.getCharacterGoals(userId, sessionId)
            call.respond(
                HttpStatusCode.OK,
                goals.map { it.toResponse() }
            )
        }

        put("/complete") {
            val request = call.receive<IdRequest>()
            val result = goalService.updateGoalStatus(request.id)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun GoalModel.toResponse() = GoalResponse(
    id = this.id,
    characterId = this.usersSessionId,
    name = this.name,
    isCompleted = this.isCompleted,
)
