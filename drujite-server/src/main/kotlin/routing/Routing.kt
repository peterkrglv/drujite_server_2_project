package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import ru.drujite.routing.*
import ru.drujite.services.ClothingService
import ru.drujite.services.ImageService
import services.*

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService,
    sessionService: SessionService,
    usersSessionsService: UsersSessionsService,
    characterService: CharacterService,
    goalService: GoalService,
    timeTableService: TimeTableService,
    clanService: ClanService,
    newsService: NewsService,
    imageService: ImageService,
    clothingService: ClothingService
) {
    val v1 = "/api/v1/"
    routing {
        route(v1 + "user") {
            userRoute(userService, jwtService)
        }

        route(v1 + "auth") {
            authRoute(jwtService)
        }

        route(v1 + "signup") {
            signupRoute(jwtService, userService)
        }

        route(v1 + "session") {
            sessionRoute(jwtService, sessionService)
        }

        route(v1 + "users-sessions") {
            usersSessionsRoute(jwtService, usersSessionsService)
        }

        route(v1 + "users-characters") {
            usersCharactersRoute(jwtService, usersSessionsService, characterService)
        }

        route(v1 + "character") {
            characterRoute(characterService)
        }

        route(v1 + "goal") {
            goalRoute(goalService, jwtService)
        }

        route(v1 + "timetable") {
            timeTableRoute(timeTableService)
        }

        route(v1 + "event") {
            eventRoute(timeTableService)
        }

        route(v1 + "clan") {
            clanRoute(clanService = clanService)
        }

        route(v1 + "news") {
            newsRoute(newsService)
        }

        route(v1 + "images") {
            imageRoute(imageService)
        }

        route(v1 + "admin") {
            adminAuthRoute(jwtService, userService)
        }

        route(v1 + "clothing-type") {
            clothingTypeRoute(clothingService)
        }

        route(v1 + "clothing-item") {
            clothingRoute(clothingService)
        }

        route(v1 + "characters-clothing") {
            charactersClothingRoute(clothingService)
        }

        get(v1) {
            call.respondText(
                """
                <html>
                    <head>
                        <title>Drujite API</title>
                        <link rel="stylesheet" type="text/css" href="/static/styles.css">
                    </head>
                    <body>
                        <h1>Welcome to Drujite API</h1>
                        <p>За южное солнце!</p>
                        <p>${application.environment.config.property("jwt.secret").getString()}</p>
                    </body>
                </html>
                """.trimIndent(),
                ContentType.Text.Html
            )
        }
    }
}
