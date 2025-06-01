import models.UserModel
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserTable : UUIDTable("users") {
    val phone = varchar("phone", 15).uniqueIndex()
    val username = varchar("username", 255)
    val password = varchar("password", 255)
    val gender = varchar("gender", 15)
    val isAdmin = bool("is_admin").default(false)
}

class UserDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDAO>(UserTable)

    var phone by UserTable.phone
    var username by UserTable.username
    var password by UserTable.password
    var gender by UserTable.gender
    var isAdmin by UserTable.isAdmin
}

fun daoToModel(dao: UserDAO) = UserModel(
    id = dao.id.value,
    phone = dao.phone,
    username = dao.username,
    password = dao.password,
    gender = dao.gender
)