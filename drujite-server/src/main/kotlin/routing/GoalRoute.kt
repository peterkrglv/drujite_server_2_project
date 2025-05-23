package routing

import db.repos_impls.EventRepositoryImpl
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.GoalModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import requests.AddGoalRequest
import requests.IdRequest
import responses.GoalResponse
import responses.IdResponse
import ru.drujite.models.GoalModelWithCharacterdId
import ru.drujite.responses.GoalModelWithCharacterIdResponse
import services.GoalService
import services.JwtService



val logger: Logger = LoggerFactory.getLogger(EventRepositoryImpl::class.java)
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
            val goalId = goalService.addGoal(request) ?: return@post call.respond(HttpStatusCode.BadRequest)
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

        get("session-all") {
            val sessionId = call.request.queryParameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            logger.info("sessionId: $sessionId")
            val goals = goalService.getSessionsGoals(sessionId)
            logger.info("goals: $goals")
            call.respond(
                HttpStatusCode.OK,
                goals.map { it.toResponse() }
            )
        }
    }
}

private fun GoalModel.toResponse() = GoalResponse(
    id = this.id,
    characterId = this.usersSessionId,
    name = this.name,
    isCompleted = this.isCompleted,
)

private fun GoalModelWithCharacterdId.toResponse() = GoalModelWithCharacterIdResponse(
    id = this.id,
    name = this.name,
    isCompleted = this.isCompleted,
    characterId = this.characterId,
)