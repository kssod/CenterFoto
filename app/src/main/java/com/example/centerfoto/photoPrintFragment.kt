package com.example.centerfoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.Toast

class PhotoPrintFragment() : BottomSheetDialogFragment() {
    private lateinit var selectPhotosButton: Button
    private lateinit var addButton: Button
    private lateinit var removeButton: Button
    private lateinit var quantityTextView: TextView
    private var currentZipPath: String? = null






    private lateinit var cartViewModel: CartViewModel // для случая синхронизации с корзиной (см. manifest и был создан класс MyApp)
  //  private val cartViewModel: CartViewModel by activityViewModels() //в случае если просто чтобы при выходе из фрагмента данные сохранялись
// если сделать как было то убрать класс MyApp
    private var currentProductOption: ProductOption? = null

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
        currentProductOption = ProductOption(
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
            currentProductOption?.let { product ->
                cartViewModel.addToBasket(product)  // Просто говорим ViewModel добавить
                // Всё! UI обновится сам через observe
            }
        }
        removeButton.setOnClickListener {
            currentProductOption?.let { product ->
                cartViewModel.removeFromBasket(product)  //
            }
        }
        selectPhotosButton.setOnClickListener {
            Toast.makeText(requireContext(), "Как-нибудь потом", Toast.LENGTH_SHORT).show()
        }

    }

    // 👇 НОВЫЙ МЕТОД: Обновление UI на основе данных из корзины
    private fun updateUI(cartList: List<ProductOption>) {
        currentProductOption?.let { product ->
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