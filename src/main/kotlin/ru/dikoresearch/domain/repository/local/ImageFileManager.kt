package ru.dikoresearch.domain.repository.local

import java.io.File

interface ImageFileManager {
    suspend fun saveToDisk(folderName: String, fileName: String, bytes: ByteArray): File?
    suspend fun loadImageFromDisk(filePath: String): File?
}