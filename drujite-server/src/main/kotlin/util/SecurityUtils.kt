package ru.drujite.util

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import org.mindrot.jbcrypt.BCrypt

object SecurityUtils {
    private const val AES_ALGORITHM = "AES"
    private const val AES_KEY_SIZE = 256
    private val secretKey: Key = generateKey()

    private fun generateKey(): Key {
        val keyGen = KeyGenerator.getInstance(AES_ALGORITHM)
        keyGen.init(AES_KEY_SIZE)
        return keyGen.generateKey()
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    fun decrypt(data: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val bytes = data.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return String(cipher.doFinal(bytes))
    }

    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    fun verifyPassword(password: String, hashed: String): Boolean = BCrypt.checkpw(password, hashed)
}