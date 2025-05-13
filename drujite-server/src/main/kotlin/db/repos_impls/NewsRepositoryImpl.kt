package db.repos_impls

import db.mapping.NewsDAO
import db.mapping.NewsTable
import db.mapping.daoToModel
import db.mapping.suspendTransaction
import db.repos.NewsRepository
import models.NewsModel
import java.time.Instant

class NewsRepositoryImpl : NewsRepository {
    override suspend fun add(news: NewsModel): Int {
        return suspendTransaction {
            val currentTime = Instant.now()
            NewsDAO.new {
                sessionId = news.sessionId
                title = news.title
                content = news.content
                time = currentTime
                imageUrl = news.imageUrl
            }.id.value
        }
    }

    override suspend fun get(id: Int): NewsModel? {
        return suspendTransaction {
            val dao = NewsDAO.findById(id)
            dao?.let { daoToModel(it) }
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return suspendTransaction {
            val dao = NewsDAO.findById(id)
            if (dao != null) {
                dao.delete()
                true
            } else {
                false
            }
        }
    }

    override suspend fun getSessionsNews(sessionId: Int): List<NewsModel> {
        return suspendTransaction {
            NewsDAO.find { NewsTable.sessionId eq sessionId }
                .map { daoToModel(it) }
        }
    }

    override suspend fun addImageUrl(id: Int, imageUrl: String): Boolean {
        return suspendTransaction {
            val dao = NewsDAO.findById(id)
            if (dao != null) {
                dao.imageUrl = imageUrl
                true
            } else {
                false
            }
        }
    }
}