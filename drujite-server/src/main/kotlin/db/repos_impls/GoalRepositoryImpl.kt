package db.repos_impls

import db.mapping.GoalDAO
import db.mapping.GoalTable
import db.mapping.suspendTransaction
import db.repos.GoalRepository
import models.GoalModel

class GoalRepositoryImpl: GoalRepository {
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
}