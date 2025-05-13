package services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import requests.LoginRequest
import ru.drujite.models.UserModel
import java.util.*

class JwtService(
    private val application: Application,
    private val userService: UserService,
) {
    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    val realm = getConfigProperty("jwt.realm")
    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    suspend fun createJwtToken(loginRequest: LoginRequest): String? {
        val foundUser: UserModel? = userService.findByPhone(loginRequest.phone)
        return if (foundUser != null && loginRequest.password == foundUser.password)
            JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", foundUser.id.toString())
                .withExpiresAt(Date(System.currentTimeMillis() + 72 * 60 * 60 * 1000))
                .sign(Algorithm.HMAC256(secret))
        else
            null
    }

    suspend fun customValidator(
        credential: JWTCredential,
    ): JWTPrincipal? {
        val id: String? = extractId(credential)
        val foundUser: UserModel? = id?.let { userService.findById(it) }
        return foundUser?.let {
            if (audienceMatches(credential))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }

    private fun audienceMatches(
        credential: JWTCredential,
    ): Boolean =
        credential.payload.audience.contains(audience)

    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()

    fun extractId(credential: JWTCredential): String? =
        credential.payload.getClaim("id").asString()

    fun extractId(principal: JWTPrincipal): String? =
        principal.payload.getClaim("id").asString()
}