package com.example.centerfoto



import android.content.ContentResolver
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipHelper {

    /**
     * Создаёт ZIP-архив из выбранных фото
     * @param contentResolver Для открытия потоков по Uri
     * @param imageUris Список Uri выбранных фото
     * @param cacheDir Временная папка приложения (context.cacheDir)
     * @return Путь к созданному ZIP-файлу или null в случае ошибки
     */
    fun createZip(
        contentResolver: ContentResolver,
        imageUris: List<Uri>,
        cacheDir: File
    ): String? {
        if (imageUris.isEmpty()) return null

        return try {
            // 1. Создаём временную папку для копий фото
            val tempDir = File(cacheDir, "temp_images_${System.currentTimeMillis()}")
            tempDir.mkdirs()

            // 2. Копируем каждое фото во временную папку
            val imageFiles = mutableListOf<File>()
            for ((index, uri) in imageUris.withIndex()) {
                val inputStream = contentResolver.openInputStream(uri)
                val fileName = "photo_${index + 1}.jpg"
                val destFile = File(tempDir, fileName)

                inputStream?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                imageFiles.add(destFile)
            }

            // 3. Создаём ZIP-архив
            val zipFile = File(cacheDir, "order_${System.currentTimeMillis()}.zip")
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
                for (imageFile in imageFiles) {
                    val entry = ZipEntry(imageFile.name)
                    zos.putNextEntry(entry)

                    FileInputStream(imageFile).use { fis ->
                        fis.copyTo(zos)
                    }
                    zos.closeEntry()
                }
            }

            // 4. Очищаем временные файлы
            imageFiles.forEach { it.delete() }
            tempDir.delete()

            // 5. Возвращаем путь к архиву
            zipFile.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}