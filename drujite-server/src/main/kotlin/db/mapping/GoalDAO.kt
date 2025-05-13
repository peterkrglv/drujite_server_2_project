package db.mapping

import models.GoalModel
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object GoalTable: IntIdTable("goals") {
    // users_session_id INT REFERENCES users_sessions(id) ON DELETE CASCADE,
    val usersSessionId = integer("users_session_id").references(UsersSessionsTable.id)
    val name = varchar("name", 255)
    val isCompleted = bool("is_completed").default(false)
}

class GoalDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GoalDAO>(GoalTable)

    var usersSessionId by GoalTable.usersSessionId
    var name by GoalTable.name
    var isCompleted by GoalTable.isCompleted
}

fun daoToModel(dao: GoalDAO) = GoalModel(
    id = dao.id.value,
    usersSessionId = dao.usersSessionId,
    name = dao.name,
    isCompleted = dao.isCompleted
)