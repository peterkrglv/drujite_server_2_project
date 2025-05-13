package ru.drujite.services

import db.repos.CharacterRepository
import db.repos.NewsRepository
import db.repos.SessionRepository
import java.io.File

class ImageService(
    private val characterRepository: CharacterRepository,
    private val newsRepository: NewsRepository,
    private val sessionRepository: SessionRepository,
    private val basePath: String = "storage/images"
) {
    suspend fun saveImage(entityType: String, id: Int, fileBytes: ByteArray, fileExtension: String = "jpg"): Boolean {
        val folderPath = "$basePath/$entityType"
        val filePath = "$folderPath/$id.$fileExtension"

        File(folderPath).mkdirs()

        File(filePath).outputStream().use { it.write(fileBytes) }

        val imageUrl = "images/$entityType/$id"
        return when (entityType) {
            "characters" -> characterRepository.addImageUrl(id, imageUrl)
            "news" -> newsRepository.addImageUrl(id, imageUrl)
            "sessions" -> sessionRepository.addImageUrl(id, imageUrl)
            else -> false
        }
    }

    fun getImagePath(entityType: String, id: Int): File? {
        val folderPath = "$basePath/$entityType"
        val folder = File(folderPath)

        val file = folder.listFiles()?.find { it.nameWithoutExtension == id.toString() }
        return file
    }
}