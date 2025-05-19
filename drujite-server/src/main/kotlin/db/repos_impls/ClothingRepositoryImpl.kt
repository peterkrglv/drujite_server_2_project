package ru.drujite.db.repos_impls

import db.mapping.*
import ru.drujite.db.repos.ClothingRepository
import ru.drujite.models.ClothingItem
import ru.drujite.models.ClothingType
import ru.drujite.models.ClothingTypeWithItems

class ClothingRepositoryImpl : ClothingRepository {
    override suspend fun addClothingType(name: String): Int {
        return suspendTransaction {
            ClothingTypeDAO.new {
                this.name = name
            }.id.value
        }
    }

    override suspend fun deleteClothingType(id: Int): Boolean {
        return suspendTransaction {
            ClothingTypeDAO.findById(id)?.delete() != null
        }
    }

    override suspend fun getClothingTypes(): List<ClothingType> {
        return suspendTransaction {
            ClothingTypeDAO.all().map {
                ClothingType(
                    id = it.id.value, name = it.name
                )
            }
        }
    }

    override suspend fun addClothingItem(clothingTypeId: Int): Int {
        return suspendTransaction {
            ClothingItemDAO.new {
                this.typeId = clothingTypeId
            }.id.value
        }
    }

    override suspend fun addImageUrl(clothingItemId: Int, imageUrl: String): Boolean {
        return suspendTransaction {
            ClothingItemDAO.findById(clothingItemId)?.let {
                it.imageUrl = imageUrl
                it.flush()
                true
            } ?: false
        }
    }

    override suspend fun addIconUrl(clothingItemId: Int, iconUrl: String): Boolean {
        return suspendTransaction {
            ClothingItemDAO.findById(clothingItemId)?.let {
                it.iconImageUrl = iconUrl
                it.flush()
                true
            } ?: false
        }
    }

    override suspend fun getClothingItemType(id: Int): Int? {
        return suspendTransaction {
            ClothingItemDAO.findById(id)?.typeId
        }
    }

    override suspend fun deleteClothingItem(id: Int): Boolean {
        return suspendTransaction {
            ClothingItemDAO.findById(id)?.delete() != null
        }
    }

    override suspend fun addClothingItemsToCharacter(characterId: Int, itemsIds: List<Int>): Boolean {
        return suspendTransaction {
            CharacterClothingDAO.find { CharacterClothingTable.characterId eq characterId }.forEach {
                it.delete()
            }
            itemsIds.forEach { itemId ->
                CharacterClothingDAO.new {
                    this.characterId = characterId
                    this.clothingItemId = itemId
                }
            }
            true
        }
    }

    override suspend fun getCharactersClothingItems(characterId: Int): List<ClothingItem> {
        return suspendTransaction {
            CharacterClothingDAO.find { CharacterClothingTable.characterId eq characterId }.mapNotNull {
                val clothingItem = it.clothingItemId ?: return@mapNotNull null
                ClothingItemDAO.findById(clothingItem)?.let { item ->
                    ClothingItem(
                        id = item.id.value,
                        typeId = item.typeId,
                        imageUrl = item.imageUrl,
                        iconUrl = item.iconImageUrl
                    )
                }
            }
        }
    }

    override suspend fun getAllClothingItems(): List<ClothingTypeWithItems> {
        return suspendTransaction {
            ClothingTypeDAO.all().map { type ->
                val items = ClothingItemDAO.find { ClothingItemTable.typeId eq type.id.value }.map {
                    ClothingItem(
                        id = it.id.value,
                        typeId = it.typeId,
                        imageUrl = it.imageUrl,
                        iconUrl = it.iconImageUrl
                    )
                }
                ClothingTypeWithItems(
                    id = type.id.value,
                    name = type.name,
                    items = items
                )
            }
        }
    }
}