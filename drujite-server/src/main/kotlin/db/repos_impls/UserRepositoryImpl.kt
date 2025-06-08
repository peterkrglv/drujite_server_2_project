package db.repos_impls

import UserDAO
import UserTable
import daoToModel
import db.mapping.suspendTransaction
import db.repos.UserRepository
import models.UserModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


val logger: Logger = LoggerFactory.getLogger(UserRepositoryImpl::class.java)

class UserRepositoryImpl : UserRepository {
    override suspend fun get(id: UUID): UserModel? {
        return suspendTransaction {
            UserDAO.findById(id)?.let { daoToModel(it) }
        }
    }

    override suspend fun getByPhone(phone: String): UserModel? {
        return suspendTransaction {
            UserDAO.find { UserTable.phone eq phone }.firstOrNull()?.let { daoToModel(it) }
        }
    }

    override suspend fun add(user: UserModel): UserModel? {
        return suspendTransaction {
            UserDAO.new {
                username = user.username
                phone = user.phone
                password = user.password
                gender = user.gender
            }.let { daoToModel(it) }
        }
    }

    override suspend fun delete(id: UUID): Boolean {
        return suspendTransaction {
            UserDAO.findById(id)?.let {
                it.delete()
                true
            } ?: false
        }
    }

    override suspend fun checkIsAdmin(id: UUID): Boolean {
        return suspendTransaction {
            UserDAO.findById(id)?.isAdmin ?: false
        }
    }

    override suspend fun makeAdmin(id: UUID): Boolean {
        return suspendTransaction {
            UserDAO.findById(id)?.let {
                it.isAdmin = true
                it.flush()
                true
            } ?: false
        }
    }
}