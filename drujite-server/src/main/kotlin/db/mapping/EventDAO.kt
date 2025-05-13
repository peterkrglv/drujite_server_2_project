package db.mapping

import models.EventModel
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.time

object EventTable: IntIdTable("events") {
    val timetableId = integer("timetable_id").references(TimeTable.id)
    val num = integer("num")
    val name = varchar("name", 255)
    val time = time("time").nullable()
    val isTitle = bool("is_title")
}

class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventTable)

    var timetableId by EventTable.timetableId
    var num by EventTable.num
    var name by EventTable.name
    var time by EventTable.time
    var isTitle by EventTable.isTitle
}

fun daoToModel(dao: EventDAO) = EventModel(
    id = dao.id.value,
    timetableId = dao.timetableId,
    num = dao.num,
    name = dao.name,
    time = dao.time.toString(),
    isTitle = dao.isTitle
)