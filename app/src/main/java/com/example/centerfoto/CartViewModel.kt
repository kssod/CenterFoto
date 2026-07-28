package com.example.centerfoto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Класс для управления корзиной: хранит список товаров и переживает поворот экрана
class CartViewModel : ViewModel() {

    // Хранилище списка товаров в корзине
    // MutableLiveData обеспечивает автоматическое уведомление UI об изменениях
    private val cartItems = MutableLiveData<List<ProductOption>>()
    val _cartItems: MutableLiveData<List<ProductOption>> = cartItems

    // Добавление товара в корзину
    //  - Если товар уже есть в корзине -> увеличиваем его количество на 1
    //  - Если товара нет -> добавляем новый товар с количеством 1
    fun addToBasket(productOption: ProductOption) {

        // Получаем текуший список товаров из хранилища
        // Если список null (корзина пуста), создаем новый пустой список
        val currentList = cartItems.value?.toMutableList() ?: mutableListOf()

        // Ищем, есть ли уже такой товар (сравниваем по id товара)
        val existingProductOption: ProductOption? = currentList.find { current -> productOption.id == current.id }



        if (existingProductOption != null) {
            val existingFiles: List<FileInfo>? = existingProductOption.tempFileInfos
            val newFiles = productOption?.tempFileInfos ?: emptyList()
            existingProductOption.tempFileInfos = existingFiles?.plus(newFiles)
            existingProductOption.quantityInBasket = existingProductOption.tempFileInfos?.size ?: 0
        } else {
            val newProduct = productOption.copy(quantityInBasket = productOption.tempFileInfos?.size ?: 0)
            currentList.add(newProduct)
        }

        // Сохраняем измененный список обратно в LiveData
        // Это автоматическим уведомит все Activity/Fragment об изменении
        cartItems.value = currentList
    }

    // Удаление товара из корзины
    fun removeFromBasket(productOption: ProductOption) {
        val currentList = cartItems.value?.toMutableList() ?: return
        val existingProductOption: ProductOption? = currentList.find {it.id == productOption.id}

        if (existingProductOption!=null) {
            if (existingProductOption.quantityInBasket > 1)
                existingProductOption.quantityInBasket--
            else currentList.remove(existingProductOption)
        }
        cartItems.value=currentList
    }

}