package com.example.centerfoto

data class Product (
    val id: Int,
    val imageRes: Int,
    val name: String,
    var price: Int,
    var quantityInBasket: Int = 0,
    var zipFilePath: String? = null)

