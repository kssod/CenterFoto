package com.example.centerfoto.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.centerfoto.CartViewModel
import com.example.centerfoto.MyApp
import com.example.centerfoto.R
import com.example.centerfoto.ZipHelper
import com.example.centerfoto.adapters.BasketAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.example.centerfoto.dataModel.CartItem

class BasketActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var adapter: BasketAdapter
    private lateinit var cartViewModel: CartViewModel
    private lateinit var checkoutButton: Button
    private lateinit var cartActivityEscape: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basket_activity)
        cartViewModel = (application as MyApp).cartViewModel
        cartActivityEscape = findViewById(R.id.cartActivityEscape)
        cartRecyclerView = findViewById(R.id.basketRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        setupRecyclerView()
        setupObservers()

        cartActivityEscape.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        checkoutButton.setOnClickListener {
            val cartList = cartViewModel._cartItems.value ?: emptyList()

            // 1. Проверяем, пуста ли корзина
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Проверяем, есть ли файлы с uri
            val hasValidFiles = cartList.any { it.fileUri != null }
            if (!hasValidFiles) {
                Toast.makeText(this, "Нет файлов для отправки", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Показываем диалог загрузки
            val progressDialog = ProgressDialog(this).apply {
                setMessage("Подготавливаем файлы...")
                setCancelable(false)
                show()
            }

            // 4. Создаём архив в фоновом потоке
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Получаем папку для сохранения
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    //  Создаём архив из CartItem
                    val zipPath = ZipHelper.createZip(
                        contentResolver = contentResolver,
                        cartItems = cartList,  // ← передаём список CartItem
                        outputDir = downloadsDir
                    )

                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()

                        if (zipPath != null) {
                            // Архив создан — отправляем
                            sendToWhatsApp(zipPath)
                        } else {
                            Toast.makeText(
                                this@BasketActivity,
                                "Ошибка создания архива",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@BasketActivity,
                            "Ошибка: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }
    private fun openFile(cartItem: CartItem) {
        val uri = cartItem.fileUri
        if (uri == null) {
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Определяем MIME-тип по расширению
        val mimeType = getMimeType(cartItem.originalFileName)

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Открыть файл"))
        } catch (e: Exception) {
            Toast.makeText(this, "Нет приложения для открытия", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Определяет MIME-тип по расширению
    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".pdf", true) -> "application/pdf"
            fileName.endsWith(".doc", true) -> "application/msword"
            fileName.endsWith(".docx", true) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            fileName.endsWith(".xls", true) -> "application/vnd.ms-excel"
            fileName.endsWith(".xlsx", true) -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            fileName.endsWith(".jpg", true) || fileName.endsWith(".jpeg", true) -> "image/jpeg"
            fileName.endsWith(".png", true) -> "image/png"
            fileName.endsWith(".gif", true) -> "image/gif"
            fileName.endsWith(".zip", true) -> "application/zip"
            fileName.endsWith(".txt", true) -> "text/plain"
            else -> "*/*"   // ← если неизвестно — показать все приложения
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
                putExtra(Intent.EXTRA_TEXT, "Заказ из приложения") // текст к файлу
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // разрешаем читать файл
                setPackage("com.whatsapp")
            }
            if (intent.resolveActivity(packageManager) != null) {
                // ✅ Запускаем напрямую, БЕЗ createChooser
                // createChooser показывает диалог выбора — он нам не нужен
                startActivity(intent)
            } else {
                // Если WhatsApp не установлен — показываем сообщение
                Toast.makeText(this, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка отправки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupRecyclerView() {
        adapter = BasketAdapter(
            onQuantityChange = { product, change ->
                when (change) {
                    1 -> cartViewModel.increaseCopies(product)
                    -1 -> cartViewModel.removeFromBasket(product)
                }
            },
            onPageRangeChange = { product, range ->
                cartViewModel.updatePageRange(product,range)
            },
            onFileClick = { cartItem ->
                openFile(cartItem)
            }

        )

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = adapter

    }

    private fun setupObservers() {
        // Наблюдаем за списком товаров
        cartViewModel._cartItems.observe(this) { cartList ->
            adapter.updateItems(cartList)


        }
    }
}