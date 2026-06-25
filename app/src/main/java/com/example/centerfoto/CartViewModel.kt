package com.example.centerfoto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Класс для управления корзиной: хранит список товаров и переживает поворот экрана
class CartViewModel : ViewModel() {

    // Хранилище списка товаров в корзине
    // MutableLiveData обеспечивает автоматическое уведомление UI об изменениях
    private val cartItems = MutableLiveData<List<Product>>()
    val _cartItems: MutableLiveData<List<Product>> = cartItems

    // Добавление товара в корзину
    //  - Если товар уже есть в корзине -> увеличиваем его количество на 1
    //  - Если товара нет -> добавляем новый товар с количеством 1
    fun addToBasket(product: Product) {

        // Получаем текуший список товаров из хранилища
        // Если список null (корзина пуста), создаем новый пустой список
        val currentList = cartItems.value?.toMutableList() ?: mutableListOf()

        // Ищем, есть ли уже такой товар (сравниваем по id товара)
        val existingProduct: Product? = currentList.find { current -> product.id == current.id }

        if (existingProduct != null) {
            // Если товар уже есть в корзине, увеличиваем счётчик
            existingProduct.quantityInBasket++
        } else {
            // Если товара нет - добавляем новый с quantity = 1
            val newProduct = product.copy(quantityInBasket = product.quantityInBasket)
            currentList.add(newProduct)
        }

        // Сохраняем измененный список обратно в LiveData
        // Это автоматическим уведомит все Activity/Fragment об изменении
        cartItems.value = currentList
    }

    // Удаление товара из корзины
    fun removeFromBasket(product: Product) {
        val currentList = cartItems.value?.toMutableList() ?: return
        val existingProduct: Product? = currentList.find {it.id == product.id}

        if (existingProduct!=null) {
            if (existingProduct.quantityInBasket > 1)
                existingProduct.quantityInBasket--
            else currentList.remove(existingProduct)
        }
        cartItems.value=currentList
    }

}