package ru.dikoresearch.data.repository.local

import org.jetbrains.exposed.sql.exposedLogger
import ru.dikoresearch.domain.repository.local.ImageFileManager
import java.io.File

class ImageFileManagerImpl(
    private val imagesStorage: String
): ImageFileManager {

    override suspend fun saveToDisk(folderName: String, fileName: String, bytes: ByteArray): File? {
        val path = "$imagesStorage/$folderName"
        val folder = File(path)

        try {
            if (!folder.exists()){
                folder.mkdirs()
            }

            val file = File(folder, fileName)
            file.writeBytes(bytes)

            org.jetbrains.exposed.sql.exposedLogger.info("File successfully stored to ${file.absolutePath}")

            return file
        }
        catch (e: Exception){
            exposedLogger.error("File Write caused exception $e")
            return null
        }


    }

    override suspend fun loadImageFromDisk(filePath: String): File? {
        val file = File(filePath)
        if (file.exists()){
            return file
        }
        else {
            return null
        }
    }
}