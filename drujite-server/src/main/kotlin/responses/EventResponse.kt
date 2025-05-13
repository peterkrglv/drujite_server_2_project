package responses

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse (
    val id: Int,
    val timetableId: Int,
    val num: Int,
    val name: String,
    val time: String?,
    val isTitle: Boolean,
)