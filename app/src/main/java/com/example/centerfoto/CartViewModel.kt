package com.example.centerfoto

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.centerfoto.dataModel.CartItem

// Класс для управления корзиной: хранит список товаров и переживает поворот экрана
class CartViewModel : ViewModel() {

    // ✅ Храним строки, а не Uri
    private val _selectedUriStrings = MutableLiveData<Set<String>>(emptySet())
    val selectedUriStrings: MutableLiveData<Set<String>> = _selectedUriStrings

    // ✅ Добавить URI (превращаем в строки)
    fun addUris(uris: List<Uri>): Set<String> {
        val oldSet = _selectedUriStrings.value ?: emptySet()
        val newSet = oldSet + uris.map { it.toString() }
        _selectedUriStrings.value = newSet

        // ✅ Возвращаем ТОЛЬКО НОВЫЕ URI
        return newSet - oldSet
    }
    // Хранилище списка товаров в корзине
    // MutableLiveData обеспечивает автоматическое уведомление UI об изменениях
    private val cartItems = MutableLiveData<List<CartItem>>()
    val _cartItems: MutableLiveData<List<CartItem>> = cartItems

    // Добавление товара в корзину
    //  - Если товар уже есть в корзине -> увеличиваем его количество на 1
    //  - Если товара нет -> добавляем новый товар с количеством 1
    fun increaseCopies(cartItem: CartItem) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
        val foundItem = currentList.find {it.id == cartItem.id}
        foundItem?.copies = foundItem?.copies?.plus(1) ?: 1
        foundItem?.pricePerCopy = foundItem!!.pricePerCopy* foundItem.copies
        _cartItems.value = currentList
    }

    fun updatePageRange(cartItem: CartItem, newRange: String) {
        val currentList = _cartItems.value?.toMutableList() ?: return
        val found = currentList.find { it.id == cartItem.id }
        found?.pageRange = newRange
        _cartItems.value = currentList  // ← сохраняем в LiveData
    }

    fun removeFromBasket (cartItem: CartItem) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
        val foundItem = currentList.find { cartItem.id == it.id } ?: return

        if (foundItem.copies == 1 ) {
           currentList.remove(foundItem)
        }
        else {
            foundItem.copies = foundItem.copies.minus(1)
        }
        _cartItems.value = currentList
    }

    fun addToBasket(cartItem: List<CartItem>) {

        // Получаем текуший список товаров из хранилища
        // Если список null (корзина пуста), создаем новый пустой список
        val currentList = cartItems.value?.toMutableList() ?: mutableListOf()

        cartItem.forEach { currentList.add(it) }
        currentList.toSet()

        // Сохраняем измененный список обратно в LiveData
        // Это автоматическим уведомит все Activity/Fragment об изменении
        cartItems.value = currentList
    }

    // Удаление товара из корзины


}