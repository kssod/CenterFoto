package com.example.centerfoto

import android.net.Uri
data class FileInfo(
    val uri: Uri,
    val categoryName: String,      // ← название подкатегории
    val originalFileName: String   // ← оригинальное имя (чтобы не доставать каждый раз)
)
data class ProductOption (
    val id: Int,
    val imageRes: Int,
    val name: String,
    var price: Int,
    var quantityInBasket: Int = 0,
    var zipFilePath: String? = null,
    var tempFileInfos: List<FileInfo>? = null
)

