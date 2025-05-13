package routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.NewsModel
import requests.AddNewsRequest
import requests.IdRequest
import responses.IdResponse
import responses.NewsResponse
import services.NewsService

fun Route.newsRoute(
    newsService: NewsService
) {
    authenticate {
        post() {
            val request = call.receive<AddNewsRequest>()
            val news = request.toModel()
            val newsId = newsService.add(news)
            call.application.environment.log.info("News $newsId added")
            call.respond(HttpStatusCode.OK, IdResponse(newsId))

        }

        delete() {
            val request = call.receive<IdRequest>()
            val result = newsService.delete(request.id)
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
            val news = newsService.get(id)
            if (news != null) {
                call.respond(HttpStatusCode.OK, news.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/session") {
            val sessionId = call.request.queryParameters["sessionId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val newsList = newsService.getSessionsNews(sessionId)
            if (newsList.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, newsList.map { it.toResponse() })
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun NewsModel.toResponse() =
    NewsResponse(
        id = id,
        sessionId = sessionId,
        title = title,
        content = content,
        dateTime = dateTime,
        imageUrl = imageUrl
    )

private fun AddNewsRequest.toModel() =
    NewsModel(
        id = 0,
        sessionId = sessionId,
        title = title,
        content = content,
        dateTime = "",
        imageUrl = imageUrl
    )