package com.example.centerfoto

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoPrintFragment() : BottomSheetDialogFragment() {
    private lateinit var selectPhotosButton: Button
    private lateinit var addButton: Button
    private lateinit var removeButton: Button
    private lateinit var quantityTextView: TextView
    private var currentZipPath: String? = null

    // Фабричная функция для выбора фотографий
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        if (uris?.isNotEmpty() == true) {
            // Фото выбрано, uri — это адрес фото в памяти телефона
            createZipArchive(uris)
        } else {
            Toast.makeText(requireContext(), "Выбор отменен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createZipArchive(uris: List<Uri>) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val zipPath = ZipHelper.createZip(
                    contentResolver = requireContext().contentResolver,
                    imageUris = uris,
                    cacheDir = downloadsDir
                )

                withContext(Dispatchers.Main) {
                    if (zipPath != null) {
                        // ✅ 1. Сохраняем путь к архиву
                        currentZipPath = zipPath

                        // ✅ 2. Обновляем товар: количество = количество фото
                        currentProduct?.let { product ->
                            val updatedProduct = product.copy(
                                quantityInBasket = uris.size,  // ← количество = число фото
                                zipFilePath = zipPath           // ← сохраняем путь к архиву
                            )

                            // ✅ 3. Добавляем в корзину
                            cartViewModel.addToBasket(updatedProduct)

                            // ✅ 4. Показываем уведомление
                            Toast.makeText(
                                requireContext(),
                                "Добавлено ${uris.size} фото в корзину",
                                Toast.LENGTH_SHORT
                            ).show()

                            // ✅ 5. Закрываем фрагмент
                            dismiss()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка создания архива", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private lateinit var cartViewModel: CartViewModel // для случая синхронизации с корзиной (см. manifest и был создан класс MyApp)
  //  private val cartViewModel: CartViewModel by activityViewModels() //в случае если просто чтобы при выходе из фрагмента данные сохранялись
// если сделать как было то убрать класс MyApp
    private var currentProduct: Product? = null

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_PRODUCT_ID = "id"
        private const val ARG_IMAGE = "image"

        fun newInstance(productId: Int, title: String, description: String, image:Int): PhotoPrintFragment {
            val fragment: PhotoPrintFragment = PhotoPrintFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            args.putInt(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_photo_print, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // убрать если хочу чтобы было здесь by activityViewModel а в корзине by viewModels
        cartViewModel = (requireContext().applicationContext as MyApp).cartViewModel
        selectPhotosButton = view.findViewById(R.id.selectPhotosButton)
        addButton = view.findViewById(R.id.addButton)
        removeButton = view.findViewById(R.id.removeButton)
        quantityTextView = view.findViewById(R.id.quantityTextView)

        // Получаем все данные о товаре
        val productId = arguments?.getInt(ARG_PRODUCT_ID) ?: 0
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        val image = arguments?.getInt(ARG_IMAGE) ?: 0

        // Создаем объект товара
        // (цену пока ставим 0)
        currentProduct = Product(
            id = productId,
            name = title,
            price = 0,
            quantityInBasket = 0,
            imageRes = image,
            zipFilePath = currentZipPath
        )


        view.findViewById<TextView>(androidx.core.R.id.text).text = title
        view.findViewById<TextView>(androidx.core.R.id.text2).text = description

        // Наблюдаем за изменениями в корзине
        cartViewModel._cartItems.observe(viewLifecycleOwner) { cartList ->
            updateUI(cartList)
        }

        setupButtons()
    }

    // 👇 НОВЫЙ МЕТОД: Выносим логику кнопок в отдельную функцию
    private fun setupButtons() {
        // Добавить товар кнопка
        addButton.setOnClickListener {
            currentProduct?.let { product ->
                cartViewModel.addToBasket(product)  // Просто говорим ViewModel добавить
                // Всё! UI обновится сам через observe
            }
        }
        removeButton.setOnClickListener {
            currentProduct?.let { product ->
                cartViewModel.removeFromBasket(product)  //
            }
        }
        selectPhotosButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }

    // 👇 НОВЫЙ МЕТОД: Обновление UI на основе данных из корзины
    private fun updateUI(cartList: List<Product>) {
        currentProduct?.let { product ->
            // Ищем наш товар в корзине
            val cartProduct = cartList.find { it.id == product.id }

            if (cartProduct != null) {
                // Товар есть в корзине
                removeButton.isVisible = true
                quantityTextView.isVisible = true
                quantityTextView.text = cartProduct.quantityInBasket.toString()
            } else {
                // Товара нет в корзине
                removeButton.isVisible = false
                quantityTextView.isVisible = false
                quantityTextView.text = ""  // Очищаем текст
            }
        }
    }
}