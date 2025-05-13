package db.repos_impls

import db.mapping.*
import db.repos.ClanRepository
import models.ClanModel

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

    override suspend fun addClanToSession(clanIdAdd: Int, sessionIdAdd: Int): Boolean {
        return suspendTransaction {
            val clan = ClanDAO.findById(clanIdAdd)
            val session = SessionDAO.findById(sessionIdAdd)
            if (clan != null && session != null) {
                SessionClansDAO.new {
                    clanId = clanIdAdd
                    sessionId = sessionIdAdd
                }.id.value > 0
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
}