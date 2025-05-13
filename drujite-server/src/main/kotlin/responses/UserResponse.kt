package ru.drujite.responces

import kotlinx.serialization.Serializable
import java.util.*
import ru.drujite.util.UUIDSerializer

@Serializable
data class UserResponse(
    val username: String,
    val phone: String
)