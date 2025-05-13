package requests

import kotlinx.serialization.Serializable

@Serializable
data class AddClanToSessionRequest (
    val clanId: Int,
    val sessionId: Int
)