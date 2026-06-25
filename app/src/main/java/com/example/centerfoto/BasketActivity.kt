package com.example.centerfoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class BasketActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var adapter: BasketAdapter
    private lateinit var cartViewModel: CartViewModel
    private lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basket_activity)
        cartViewModel = (application as MyApp).cartViewModel

        cartRecyclerView = findViewById(R.id.basketRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        setupRecyclerView()
        setupObservers()

        checkoutButton.setOnClickListener() {
            val cartList = cartViewModel._cartItems.value ?: emptyList()
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 3. Проверяем, есть ли у товаров прикрепленные файлы
            val hasFiles = cartList.any { !it.zipFilePath.isNullOrEmpty() }

            // 4. Если файлы есть → отправляем в WhatsApp
            if (hasFiles) {
                val firstFilePath = cartList.first { !it.zipFilePath.isNullOrEmpty() }.zipFilePath
                firstFilePath?.let { filePath ->
                    sendToWhatsApp(filePath)
                }
            }

        }
    }
    private fun sendToWhatsApp(filePath: String) {
        try {
            // 1. Создаем объект файла по пути
            val file = File(filePath)

            // 2. Проверяем, существует ли файл
            if (!file.exists()) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
                return
            }

            // 3. Получаем URI через FileProvider (для Android 7+)
            val fileUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )

            // 4. Создаем Intent для отправки
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"                          // тип файла
                putExtra(Intent.EXTRA_STREAM, fileUri)           // прикрепляем файл
                putExtra(Intent.EXTRA_TEXT, "Заказ из CenterFoto") // текст к файлу
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // разрешаем читать файл
            }

            // 5. Пытаемся открыть WhatsApp
           // intent.setPackage("org.telegram.messenger")
            startActivity(Intent.createChooser(intent, "Отправить архив"))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка отправки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupRecyclerView() {
        adapter = BasketAdapter() { product, change ->
            when (change) {
                1 -> cartViewModel.addToBasket(product)
                -1 -> cartViewModel.removeFromBasket(product)}
        }

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Наблюдаем за списком товаров
        cartViewModel._cartItems.observe(this) { cartList ->
            adapter.updateProducts(cartList)


        }
    }
}