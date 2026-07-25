package com.example.centerfoto

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.centerfoto.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptionsListFragment: BottomSheetDialogFragment() {
    //private lateinit var descriptionText: TextView
    //private lateinit var priceText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var attachButton: ImageButton

    companion object {
        private const val ARG_SERVICE_ID = "service_id"
        private const val ARG_IMAGE = "image"

        fun newInstance(serviceId: Int, imageRes: Int) : OptionsListFragment {
            val fragment = OptionsListFragment()
            val args = Bundle()
            args.putInt(ARG_SERVICE_ID, serviceId)
            args.putInt(ARG_IMAGE, imageRes)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var cartViewModel: CartViewModel
    private var currentProduct: Product? = null
    private var currentZipPath: String? = null

    private val pickDocsLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri>? ->
        if (uris?.isNotEmpty() == true)
            createZipArchive(uris)
        else
            Toast.makeText(requireContext(), "Выбор отменён", Toast.LENGTH_SHORT).show()
    }

    private fun createZipArchive(uris: List<Uri>) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val categoryName = currentProduct?.name ?: "Файлы"
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val zipPath = ZipHelper.createZip(
                    contentResolver = requireContext().contentResolver,
                    imageUris = uris,
                    cacheDir = downloadsDir,
                    categoryName = categoryName
                )
                withContext(Dispatchers.Main) {
                if (zipPath != null) {
                    // ✅ 1. Сохраняем путь к архиву
                    currentZipPath = zipPath

                    // ✅ 2. Обновляем товар: количество = количество фото
                    currentProduct?.let { product: Product ->
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

                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка создания архива", Toast.LENGTH_SHORT)
                        .show()
                }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_options_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        cartViewModel = (requireContext().applicationContext as MyApp).cartViewModel
        val serviceID = arguments?.getInt(ARG_SERVICE_ID) ?: 0
        val imageRes = arguments?.getInt(ARG_IMAGE) ?: 0

        optionsContainer = view.findViewById(R.id.optionsContainer)

        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.isHideable = true
                behavior.skipCollapsed = true
                val scrollView = view.findViewById<ScrollView>(R.id.scrollView)
                scrollView?.viewTreeObserver?.addOnScrollChangedListener {
                    // Если ScrollView не в самом верху (прокручен вниз) — запрещаем свайп
                    behavior.isDraggable = scrollView.scrollY == 0
                }
            }
        }


        val options = getOptionsForService(serviceID, imageRes)

        for (option in options) {
            val card = LayoutInflater.from(requireContext())
                .inflate(R.layout.option_item_card, optionsContainer, false)

            // Находим TextView внутри карточки
            val descriptionText = card.findViewById<TextView>(R.id.optionDescriptionTextView)
            val priceText = card.findViewById<TextView>(R.id.optionPriceTextView)
            attachButton = card.findViewById(R.id.attachButton)
            // Заполняем данными
            descriptionText.text = option.name
            priceText.text = "${option.price} тг"

            attachButton.setOnClickListener() {
                currentProduct = option
                val mimeTypes = when (serviceID) {
                    1 -> arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
                    2 -> arrayOf("image/*")
                    else -> arrayOf("*/*")
                }
                pickDocsLauncher.launch(mimeTypes) }
            optionsContainer.addView(card)


        }
    }
}
private fun getOptionsForService(serviceId: Int, imageRes: Int): List<Product> {
    return when (serviceId) {
        // Ксерокопия (id = 1)
        1 -> listOf(
            Product(101, imageRes, "Чёрно-белая А4 односторонняя", 40),
            Product(102, imageRes,"Чёрно-белая А4 двухсторонняя", 70),
            Product(103, imageRes,"Чёрно-белая А3 односторонняя",  90),
            Product(104, imageRes,"Чёрно-белая А3 двухсторонняя",  150),
            Product(105, imageRes,"Цветная А4 односторонняя", 140),
            Product(106, imageRes,"Цветная А4 двухсторонняя", 260),
            Product(107, imageRes,"Цветная А3 односторонняя",  360),
            Product(108, imageRes,"Цветная А3 двухсторонняя",  550)
        )
        2 -> listOf(
            Product(101, imageRes, "10х15 (А6) глянцевая бумага", 85),
            Product(102, imageRes,"10х15 (А6) сатиновая бумага", 140),
            Product(103, imageRes,"15х21 (А5) глянцевая бумага",  300),
            Product(104, imageRes,"21х30 (А4) глянцевая бумага",  420),
            Product(105, imageRes,"30х42 (А3) глянцевая бумага", 720),
            Product(105, imageRes,"42х60 (А2) глянцевая бумага", 720),
            Product(105, imageRes,"60х85 (А1) глянцевая бумага", 720),
            Product(105, imageRes,"85х118 (А0) глянцевая бумага", 720),


        )
        // Сканирование (id = 6)

        else -> emptyList()
    }
}

