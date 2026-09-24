package com.example.centerfoto.dataModel

import android.net.Uri
data class ProductOption(
    val imageRes: Int = 0,
    val name: String = "",
    var price: Int = 0,
    val serviceId: Int = 0
)

data class businesCardsCartItem(
    val pricePerUnit: Int,
    val maxQuantity: Int,
    val minQuantity: Int,
    val bCardFiles: List<bCardUris>
)

data class bCardUris(
    val filename: String,
    val uris: List<Uri>
)

data class CartItem(
    val id: Int,                  // ← уникальный ID для файла
    val image: Int?,
    val originalFileName: String,         // ← имя файла ("report.pdf")
    val categoryName: String?,     // ← подкатегория ("Черно-белая А4")
    var pricePerCopy: Int,        // ← цена за 1 экземпляр
    var copies : Int = 1,          // ← количество экземпляров
    val fileUri: Uri? = null,
    var pageRange: String = "",
    val serviceId: Int = 0,
) {

        // Функция обновляет finalName в зависимости от copie
        fun updateFinalName(): String {
            // 1. Отделяем имя от расширения
            val nameWithoutExt = originalFileName.substringBeforeLast(".")
            val ext = originalFileName.substringAfterLast(".", "")

            val categoryNamePart = if (categoryName?.isNotEmpty()==true) {categoryName} else ""
            // 2. Количество экземпляров
            val copiesPart = if (copies > 1) "${copies}экз" else ""

            // 3. Диапазон страниц
            val pageRangePart = if (pageRange.isNotEmpty()) "стр.(${pageRange})" else ""

            // 4. Собираем имя
            return if (categoryNamePart.isNotEmpty() || copiesPart.isNotEmpty() || pageRangePart.isNotEmpty()) {
                "${categoryNamePart}___${copiesPart}___${pageRangePart}___$nameWithoutExt.$ext"
            } else {
                originalFileName
            }
        }
    fun isImage(): Boolean {
        val fileName = originalFileName.lowercase()
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif")
    }
}

