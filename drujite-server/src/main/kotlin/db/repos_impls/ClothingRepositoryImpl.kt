package ru.drujite.db.repos_impls

import db.mapping.*
import ru.drujite.db.repos.ClothingRepository
import ru.drujite.models.ClothingItem
import ru.drujite.models.ClothingType
import ru.drujite.models.ClothingTypeWithItems

class ClothingRepositoryImpl : ClothingRepository {
    override suspend fun addClothingType(clothingType: ClothingType): Int {
        return suspendTransaction {
            ClothingTypeDAO.new {
                this.name = clothingType.name
                this.isEditable = clothingType.isEditable
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
                    id = it.id.value, name = it.name, isEditable = it.isEditable
                )
            }
        }
    }

    override suspend fun getEditableClothingTypes(): List<ClothingType> {
        return suspendTransaction {
            ClothingTypeDAO.find { ClothingTypeTable.isEditable eq true }.map {
                ClothingType(
                    id = it.id.value, name = it.name, isEditable = it.isEditable
                )
            }
        }
    }

    override suspend fun addClothingItem(clothingItem: ClothingItem): Int {
        return suspendTransaction {
            ClothingItemDAO.new {
                this.name = clothingItem.name
                this.typeId = clothingItem.typeId
            }.id.value
        }
    }

    override suspend fun addImageUrl(clothingItemId: Int, imageUrl: String): Boolean {
        return suspendTransaction {
            ClothingItemDAO.findById(clothingItemId)?.let {
                it.imageUrl = imageUrl
                it.flush()
                true
            } == true
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
                        name = item.name,
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
                        name = it.name,
                        iconUrl = it.iconImageUrl
                    )
                }
                ClothingTypeWithItems(
                    id = type.id.value,
                    name = type.name,
                    items = items,
                    isEditable = type.isEditable
                )
            }
        }
    }

    override suspend fun getCharactersEditableClothingItems(characterId: Int): List<ClothingItem> {
        return suspendTransaction {
            CharacterClothingDAO.find { CharacterClothingTable.characterId eq characterId }.mapNotNull {
                val clothingItem = it.clothingItemId ?: return@mapNotNull null
                ClothingItemDAO.findById(clothingItem)?.let { item ->
                    val type = ClothingTypeDAO.findById(item.typeId)
                    if (type?.isEditable == true) {
                        ClothingItem(
                            id = item.id.value,
                            typeId = item.typeId,
                            name = item.name,
                            imageUrl = item.imageUrl,
                            iconUrl = item.iconImageUrl
                        )
                    } else null
                }
            }
        }
    }
}