package requests

import kotlinx.serialization.Serializable

@Serializable
data class AddGoalRequest (
    val userId: String,
    val sessionId: Int,
    val name: String,
    val isCompleted: Boolean = false,
)