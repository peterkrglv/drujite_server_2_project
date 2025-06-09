package ru.drujite.services

import ru.drujite.db.repos.ClothingRepository
import ru.drujite.models.ClothingItem
import ru.drujite.models.ClothingType
import ru.drujite.models.ClothingTypeWithItems

class ClothingService(
    private val clothingRepository: ClothingRepository,
) {
    suspend fun addClothingType(clothingType: ClothingType): Int {
        return clothingRepository.addClothingType(clothingType)
    }

    suspend fun deleteClothingType(id: Int): Boolean {
        return clothingRepository.deleteClothingType(id)
    }

    suspend fun getClothingTypes(): List<ClothingType> {
        return clothingRepository.getClothingTypes()
    }

    suspend fun addClothingItem(clothingItem: ClothingItem): Int {
        return clothingRepository.addClothingItem(clothingItem)
    }

    suspend fun addImageUrl(id: Int, imageUrl: String): Boolean {
        return clothingRepository.addImageUrl(id, imageUrl)
    }

    suspend fun addIconUrl(id: Int, iconUrl: String): Boolean {
        return clothingRepository.addIconUrl(id, iconUrl)
    }

    suspend fun deleteClothingItem(id: Int): Boolean {
        return clothingRepository.deleteClothingItem(id)
    }

    suspend fun addClothingItemsToCharacter(characterId: Int, itemsIds: List<Int>): Boolean {
        return clothingRepository.addClothingItemsToCharacter(characterId, itemsIds)
    }

    suspend fun getCharactersClothing(characterId: Int): List<ClothingItem> {
        return clothingRepository.getCharactersClothingItems(characterId)
    }

    suspend fun getAllClothingItems(): List<ClothingTypeWithItems> {
        return clothingRepository.getAllClothingItems()
    }

    suspend fun getAllEditableClothingItems(): List<ClothingTypeWithItems> {
        return clothingRepository.getAllEditableClothingItems()
    }
}