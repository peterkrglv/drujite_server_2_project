package db.mapping

import models.ClanModel
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ClanTable : IntIdTable("clans") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
}

class ClanDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ClanDAO>(ClanTable)
    var name by ClanTable.name
    var description by ClanTable.description
}

fun daoToModel(dao: ClanDAO) = ClanModel(
    id = dao.id.value,
    name = dao.name,
    description = dao.description
)