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
                logger.info("Starting getSessionsGoals with sessionId: $sessionId")
                val results = (GoalTable innerJoin UsersSessionsTable)
                    .select(UsersSessionsTable.sessionId eq sessionId)
                    .also { logger.info("Query returned ${it.count()} rows") }
                    .mapNotNull { row ->
                        try {
                            logger.info("Processing row: $row")
                            row[UsersSessionsTable.characterId]?.let { characterId ->
                                val goal = GoalModelWithCharacterdId(
                                    id = row[GoalTable.id].value,
                                    name = row[GoalTable.name],
                                    isCompleted = row[GoalTable.isCompleted],
                                    characterId = characterId
                                )
                                logger.info("Mapped goal: $goal")
                                goal
                            }
                        } catch (e: Exception) {
                            logger.error("Error processing row: $row, exception: ${e.message}", e)
                            null
                        }
                    }

                logger.info("Finished processing rows. Total goals: ${results.size}")
                results
            } catch (e: Exception) {
                logger.error("Error in getSessionsGoals: ${e.message}", e)
                emptyList()
            }
        }
    }
}