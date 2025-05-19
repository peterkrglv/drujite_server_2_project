package ru.drujite.db.repos

import ru.drujite.models.ClothingItem
import ru.drujite.models.ClothingType
import ru.drujite.models.ClothingTypeWithItems

interface ClothingRepository {
    suspend fun addClothingType(name: String): Int
    suspend fun deleteClothingType(id: Int): Boolean
    suspend fun getClothingTypes(): List<ClothingType>
    suspend fun addClothingItem(
        clothingTypeId: Int,
    ): Int

    suspend fun addImageUrl(
        clothingItemId: Int,
        imageUrl: String
    ): Boolean

    suspend fun addIconUrl(
        clothingItemId: Int,
        iconUrl: String
    ): Boolean

    suspend fun getClothingItemType(id: Int): Int?
    suspend fun deleteClothingItem(id: Int): Boolean
    suspend fun addClothingItemsToCharacter(
        characterId: Int,
        itemsIds: List<Int>
    ): Boolean

    suspend fun getCharactersClothingItems(characterId: Int): List<ClothingItem>
    suspend fun getAllClothingItems(): List<ClothingTypeWithItems>
}
