package db.repos_impls

import db.mapping.*
import db.repos.ClanRepository
import models.ClanModel
import org.jetbrains.exposed.sql.and

class ClanRepositoryImpl : ClanRepository {
    override suspend fun add(clan: ClanModel): Int {
        return suspendTransaction {
            ClanDAO.new {
                name = clan.name
                description = clan.description
            }.id.value
        }
    }

    override suspend fun get(id: Int): ClanModel? {
        return suspendTransaction {
            val clan = ClanDAO.findById(id)
            clan?.let { daoToModel(it) }
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return suspendTransaction {
            (ClanDAO.findById(id))?.delete() != null
        }
    }

    override suspend fun addClanToSession(clanId: Int, sessionId: Int): Boolean {
        return suspendTransaction {
            val clan = ClanDAO.findById(clanId)
            val session = SessionDAO.findById(sessionId)
            if (clan != null && session != null) {
                SessionClansDAO.new {
                    this.clanId = clanId
                    this.sessionId = sessionId
                }.id.value > 0
            } else {
                false
            }
        }
    }

    override suspend fun deleteClanFromSession(clanId: Int, sessionId: Int): Boolean {
        return suspendTransaction {
            val sessionClan = SessionClansDAO.find {
                (SessionsClansTable.clanId eq clanId) and (SessionsClansTable.sessionId eq sessionId)
            }.firstOrNull()
            if (sessionClan != null) {
                sessionClan.delete()
                true
            } else {
                false
            }
        }
    }

    override suspend fun getSessionsClans(sessionId: Int): List<ClanModel> {
        return suspendTransaction {
            SessionClansDAO.find { SessionsClansTable.sessionId eq sessionId }
                .mapNotNull { sessionClan ->
                    ClanDAO.findById(sessionClan.clanId)?.let { daoToModel(it) }
                }
        }
    }

    override suspend fun getAll(): List<ClanModel> {
        return suspendTransaction {
            ClanDAO.all().map { daoToModel(it) }
        }
    }
}