package services

import db.repos.UserRepository
import ru.drujite.models.UserModel
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
        return userRepository.add(user)
    }

    suspend fun checkIsAdmin(id: String): Boolean {
        return userRepository.checkIsAdmin(UUID.fromString(id))
    }

    suspend fun makeAdmin(id: String): Boolean {
        return userRepository.makeAdmin(UUID.fromString(id))
    }
}