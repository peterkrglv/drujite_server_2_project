package services

import db.repos.CharacterRepository
import db.repos.SessionRepository
import db.repos.UsersSessionsRepository
import models.CharacterModel
import models.SessionModel
import ru.drujite.util.getRedirectedUrl
import java.util.*

class UsersSessionsService(
    private val usersSessionsRepository: UsersSessionsRepository,
    private val characterRepository: CharacterRepository,
    private val sessionRepository: SessionRepository
) {
    suspend fun addUserSession(
        userId: String,
        sessionId: Int,
        characterId: Int? = null
    ) = usersSessionsRepository.addUserSession(
        userId = UUID.fromString(userId),
        sessionId = sessionId,
        characterId = characterId
    )

    suspend fun addCharacter(
        userId: String,
        sessionId: Int,
        characterId: Int
    ) = usersSessionsRepository.addCharacter(
        userId = UUID.fromString(userId),
        sessionId = sessionId,
        characterId = characterId
    )

    suspend fun deleteUserSession(
        userId: String,
        sessionId: Int
    ) = usersSessionsRepository.deleteUserSession(
        userId = UUID.fromString(userId),
        sessionId = sessionId
    )

    suspend fun deleteCharacter(
        userId: String,
        sessionId: Int,
        characterId: Int
    ) = usersSessionsRepository.deleteCharacter(
        userId = UUID.fromString(userId),
        sessionId = sessionId,
        characterId = characterId
    )

    suspend fun getCharacter(
        userId: String,
        sessionId: Int
    ): CharacterModel? {
        val characterId = usersSessionsRepository.getCharacterId(
            userId = UUID.fromString(userId),
            sessionId = sessionId
        )
        return characterId?.let { characterRepository.get(it) }
    }

    suspend fun getCharacters(
        userId: String
    ): List<CharacterModel> {
        val characterIds = usersSessionsRepository.getCharacterIds(
            userId = UUID.fromString(userId)
        )
        return characterIds.mapNotNull { characterRepository.get(it) }
    }

    suspend fun getUsersSessions(
        userId: String
    ) = usersSessionsRepository.getUsersSessions(
        userId = UUID.fromString(userId),
    )

    suspend fun getSessionsCharacters(
        sessionId: Int
    ): List<CharacterModel> {
        val characterIds = usersSessionsRepository.getSessionsCharactersIds(sessionId)
        return characterIds.mapNotNull { characterRepository.get(it) }
    }

    suspend fun addUsersSessionByQr(
        userId: String,
        qr: String
    ): SessionModel? {
        val redirectedQrLink = getRedirectedUrl(qr)
        return redirectedQrLink?.let {
            val session = sessionRepository.getSessionByQr(redirectedQrLink)
            session?.let {
                usersSessionsRepository.addUserSession(
                    userId = UUID.fromString(userId),
                    sessionId = session.id
                )
                session
            }
        }
    }
}