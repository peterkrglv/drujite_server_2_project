package ru.drujite.models

import kotlinx.serialization.Serializable


@Serializable
data class ClothingTypeWithItems(
    val id: Int,
    val name: String,
    val items: List<ClothingItem>
)