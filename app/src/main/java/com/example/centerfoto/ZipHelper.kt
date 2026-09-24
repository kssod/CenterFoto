package com.example.centerfoto

import android.content.ContentResolver
import com.example.centerfoto.dataModel.CartItem
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipHelper {

    /**
     * Создаёт ZIP-архив из списка CartItem
     * @param contentResolver Для открытия потоков по Uri
     * @param cartItems Список CartItem (каждый содержит uri, fileName, categoryName)
     * @param outputDir Папка для сохранения архива
     * @return Путь к созданному ZIP-файлу или null в случае ошибки
     */
    fun createZip(
        contentResolver: ContentResolver,
        cartItems: List<CartItem>,
        outputDir: File
    ): String? {
        if (cartItems.isEmpty()) return null

        return try {
            val zipFile = File(outputDir, "Заказ_${System.currentTimeMillis()}.zip")

            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
                for (cartItem in cartItems) {
                    val uri = cartItem.fileUri ?: continue
                    val inputStream = contentResolver.openInputStream(uri) ?: continue

                    val entry = ZipEntry(cartItem.updateFinalName())
                    zos.putNextEntry(entry)

                    inputStream.use { input ->
                        input.copyTo(zos)
                    }
                    zos.closeEntry()
                }
            }

            zipFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}