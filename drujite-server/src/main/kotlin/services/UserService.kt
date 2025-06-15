package services

import db.repos.UserRepository
import models.UserModel
import java.util.*

class UserService (
    private val userRepository: UserRepository
) {
    suspend fun findById(id: String): UserModel? {
        return userRepository.get(UUID.fromString(id))
    }

    suspend fun findByPhone(phone: String): UserModel? {
        return userRepository.getByPhone(phone)
    }

    suspend fun addUser(user: UserModel): UserModel? {
        val foundUser: UserModel? = userRepository.getByPhone(user.phone)
        if (foundUser != null) {
            return null
        }
        println("UserService Adding user: $user")
        return userRepository.add(user)
    }

    suspend fun checkIsAdmin(id: String): Boolean {
        return userRepository.checkIsAdmin(UUID.fromString(id))
    }

    suspend fun makeAdmin(uuid: UUID): Boolean {
        return userRepository.makeAdmin(uuid)
    }

    suspend fun checkIfSuperAdmin(id: String): Boolean {
        return userRepository.checkIfSuperAdmin(UUID.fromString(id))
    }

    suspend fun getAllUsers(): List<UserModel> {
        return userRepository.getAllUsers()
    }
}