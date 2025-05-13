package db.repos

import models.SessionModel
import java.util.*

interface SessionRepository {
    suspend fun get(id: Int): SessionModel?
    suspend fun add(session: SessionModel): Int
    suspend fun delete(id: Int): Boolean
    suspend fun getSessionsByUserId(userId: UUID): List<SessionModel>
    suspend fun getAll(): List<SessionModel>
    suspend fun addImageUrl(sessionId: Int, imageUrl: String): Boolean
}