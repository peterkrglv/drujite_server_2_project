package db.mapping

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ClothingTypeTable: IntIdTable("clothing") {
    val name = varchar("name", 255)
}

class ClothingTypeDAO(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<ClothingTypeDAO>(ClothingTypeTable)

    var name by ClothingTypeTable.name
}