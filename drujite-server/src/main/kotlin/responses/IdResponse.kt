package responses

import kotlinx.serialization.Serializable

@Serializable
data class IdResponse (
    val id: Int
)