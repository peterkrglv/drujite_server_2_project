package ru.drujite.models

import kotlinx.serialization.Serializable

@Serializable
data class ClothingItem (
    val id: Int,
    val typeId: Int,
    val imageUrl: String?,
    val iconUrl: String?
)