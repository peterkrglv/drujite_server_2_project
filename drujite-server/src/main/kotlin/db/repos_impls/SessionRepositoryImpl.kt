package db.repos_impls

import db.mapping.*
import db.repos.SessionRepository
import models.SessionModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SessionRepositoryImpl : SessionRepository {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    override suspend fun get(id: Int): SessionModel? {
        return suspendTransaction {
            SessionDAO.findById(id)?.let(::daoToModel)
        }
    }

    override suspend fun add(session: SessionModel): Int {
        return suspendTransaction {
            SessionDAO.new {
                name = session.name
                description = session.description
                startDate = LocalDateTime.parse(session.startDate, formatter)
                endDate = LocalDateTime.parse(session.endDate, formatter)
                imageUrl = session.imageUrl
            }.id.value
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return suspendTransaction {
            SessionDAO.findById(id)?.delete() != null
        }
    }

    override suspend fun getSessionsByUserId(userId: UUID): List<SessionModel> {
        return suspendTransaction {
            UsersSessionsDAO.find { UsersSessionsTable.userId eq userId }
                .mapNotNull { it.sessionId.let(SessionDAO::findById)?.let(::daoToModel) }
        }
    }

    override suspend fun getAll(): List<SessionModel> {
        return suspendTransaction {
            SessionDAO.all().map(::daoToModel)
        }
    }

    override suspend fun addImageUrl(sessionId: Int, imageUrl: String): Boolean {
        return suspendTransaction {
            SessionDAO.findById(sessionId)?.let {
                it.imageUrl = imageUrl
                it.flush()
                true
            } ?: false
        }
    }
}