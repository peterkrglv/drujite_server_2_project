package ru.drujite.util

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import org.mindrot.jbcrypt.BCrypt
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {
    private const val AES_ALGORITHM = "AES"
    private const val AES_KEY_SIZE = 256
    private val secretKey: Key = getKeyFromEnv()

    private fun getKeyFromEnv(): Key {
//        val keyBase64 = System.getenv("AES_SECRET_KEY")
//            ?: throw IllegalStateException("AES_SECRET_KEY is not set in environment variables")
        val keyBase64 = "yAcrjpIIh2ga7K3aazWcgw=="
        val keyBytes = Base64.getDecoder().decode(keyBase64)
        return SecretKeySpec(keyBytes, AES_ALGORITHM)
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