package db.repos_impls

import db.mapping.GoalDAO
import db.mapping.GoalTable
import db.mapping.UsersSessionsTable
import db.mapping.suspendTransaction
import db.repos.GoalRepository
import models.GoalModel
import ru.drujite.models.GoalModelWithCharacterdId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class GoalRepositoryImpl : GoalRepository {

    private val logger: Logger = LoggerFactory.getLogger(GoalRepositoryImpl::class.java)
    override suspend fun add(goal: GoalModel): Int {
        return suspendTransaction {
            GoalDAO.new {
                usersSessionId = goal.usersSessionId
                name = goal.name
                isCompleted = goal.isCompleted
            }.id.value
        }
    }

    override suspend fun get(id: Int): GoalModel? {
        return suspendTransaction {
            GoalDAO.findById(id)?.let {
                GoalModel(
                    id = it.id.value,
                    usersSessionId = it.usersSessionId,
                    name = it.name,
                    isCompleted = it.isCompleted
                )
            }
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return suspendTransaction {
            GoalDAO.findById(id)?.let {
                it.delete()
                true
            } ?: false
        }
    }

    override suspend fun getCharacterGoals(usersSessionId: Int): List<GoalModel> {
        return suspendTransaction {
            GoalDAO.find { GoalTable.usersSessionId eq usersSessionId }.map {
                GoalModel(
                    id = it.id.value,
                    usersSessionId = it.usersSessionId,
                    name = it.name,
                    isCompleted = it.isCompleted
                )
            }
        }
    }

    override suspend fun changeStatus(id: Int): Boolean {
        return suspendTransaction {
            GoalDAO.findById(id)?.let {
                it.isCompleted = !it.isCompleted
                it.flush()
                true
            } ?: false
        }
    }

    override suspend fun getSessionsGoals(sessionId: Int): List<GoalModelWithCharacterdId> {
        return suspendTransaction {
            try {
                val userSessions = UsersSessionsTable
                    .select(UsersSessionsTable.sessionId eq sessionId).associate { row ->
                        row[UsersSessionsTable.id].value to row[UsersSessionsTable.characterId]
                    }

                GoalTable
                    .select(GoalTable.usersSessionId inList userSessions.keys)
                    .mapNotNull { row ->
                        val usersSessionId = row[GoalTable.usersSessionId]
                        val characterId = userSessions[usersSessionId]
                        if (characterId != null) {
                            GoalModelWithCharacterdId(
                                id = row[GoalTable.id].value,
                                name = row[GoalTable.name],
                                isCompleted = row[GoalTable.isCompleted],
                                characterId = characterId
                            )
                        } else {
                            null
                        }
                    }
            } catch (e: Exception) {
                logger.info("Error in getSessionsGoals: ${e.message}")
                emptyList()
            }
        }
    }
}