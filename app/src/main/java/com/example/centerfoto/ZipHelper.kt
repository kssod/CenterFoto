package com.example.centerfoto

import android.content.ContentResolver
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipHelper {

    fun createZip(
        contentResolver: ContentResolver,
        imageUris: List<Uri>,
        cacheDir: File,
        categoryName: String = ""
    ): String? {
        if (imageUris.isEmpty()) return null

        return try {
            val tempDir = File(cacheDir, "temp_files_${System.currentTimeMillis()}")
            tempDir.mkdirs()

            val filesToZip = mutableListOf<File>()

            for (uri in imageUris) {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    println("Не удалось открыть поток для $uri")
                    continue
                }

                // ✅ 1. ПОЛУЧАЕМ ОРИГИНАЛЬНОЕ ИМЯ ФАЙЛА
                val originalFileName = getOriginalFileName(contentResolver, uri)
                    ?: "file_${System.currentTimeMillis()}"  // если имя не найдено — даём своё
                val finalFileName = if (categoryName.isNotEmpty()) {
                    val nameWithoutExtension = originalFileName.substringBeforeLast(".")
                    val extension = originalFileName.substringAfterLast(".", "")
                    if (extension.isNotEmpty()) {
                        "${categoryName}_$nameWithoutExtension.$extension"
                    } else {
                        "${categoryName}_$originalFileName"
                    }
                } else {
                    originalFileName
                }
                // ✅ 2. СОХРАНЯЕМ С ОРИГИНАЛЬНЫМ ИМЕНЕМ (расширение сохраняется!)
                val destFile = File(tempDir, finalFileName)

                inputStream.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                filesToZip.add(destFile)
            }

            if (filesToZip.isEmpty()) {
                tempDir.delete()
                return null
            }

            val zipFile = File(cacheDir, "order_${System.currentTimeMillis()}.zip")
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
                for (file in filesToZip) {
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)

                    FileInputStream(file).use { fis ->
                        fis.copyTo(zos)
                    }
                    zos.closeEntry()
                }
            }

            filesToZip.forEach { it.delete() }
            tempDir.delete()

            zipFile.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Получает оригинальное имя файла из URI
     */
    private fun getOriginalFileName(contentResolver: ContentResolver, uri: Uri): String? {
        var fileName: String? = null

        // Пытаемся получить имя через ContentResolver (работает для фото из галереи)
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        // Если не получилось — пробуем вытащить имя из самого URI
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast("/")
        }

        return fileName
    }
}