package db.mapping

import models.UserModel
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import ru.drujite.util.SecurityUtils
import java.util.*


object UserTable : UUIDTable("users") {
    val phone = varchar("phone", 512).uniqueIndex()
    val username = varchar("username", 512)
    val password = varchar("password", 255)
    val gender = varchar("gender", 15)
    val isAdmin = bool("is_admin").default(false)
    val isSuperAdmin = bool("is_superadmin").default(false)
}

class UserDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDAO>(UserTable)

    var phone by UserTable.phone.transform(
        { value ->
            println("Encrypting phone (to database): $value")
            SecurityUtils.encrypt(value)
        },
        { value ->
            println("Decrypting phone (from database): $value")
            SecurityUtils.decrypt(value)
        }
    )
    var username by UserTable.username.transform(
        { SecurityUtils.encrypt(it) },
        { SecurityUtils.decrypt(it) }
    )
    var password by UserTable.password.transform(
        { SecurityUtils.hashPassword(it) },
        { it }
    )
    var gender by UserTable.gender
    var isAdmin by UserTable.isAdmin
    var isSuperAdmin by UserTable.isSuperAdmin
}

fun daoToModel(dao: UserDAO) = UserModel(
    id = dao.id.value,
    phone = dao.phone,
    username = dao.username,
    password = dao.password,
    gender = dao.gender,
    isAdmin = dao.isAdmin
)