package com.example.centerfoto

import android.net.Uri
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class OptionsListFragment: BottomSheetDialogFragment() {
    //private lateinit var descriptionText: TextView
    //private lateinit var priceText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var attachButton: ImageButton
    private lateinit var goToCart: Button

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
    private var currentProductOption: ProductOption? = null
    private var currentZipPath: String? = null
    private fun getOriginalFileName(uri: Uri): String? {
        var fileName: String? = null
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast("/")
        }
        return fileName
    }
    private val pickDocsLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri>? ->
        if (uris?.isNotEmpty() == true) {
            val categoryName = currentProductOption?.name ?: "Файлы"
            Toast.makeText(requireContext(), "Файлы прикреплёны", Toast.LENGTH_SHORT).show()
            val fileInfos = uris.map { uri ->
                val originalName = getOriginalFileName(uri) ?: "file_${System.currentTimeMillis()}"

                // ✅ ДОБАВЛЯЕМ КАТЕГОРИЮ К ИМЕНИ ФАЙЛА
                val finalName = if (categoryName.isNotEmpty()) {
                    val nameWithoutExt = originalName.substringBeforeLast(".")
                    val ext = originalName.substringAfterLast(".", "")
                    if (ext.isNotEmpty()) {
                        "${categoryName}_$nameWithoutExt.$ext"
                    } else {
                        "${categoryName}_$originalName"
                    }
                } else {
                    originalName
                }

                FileInfo(uri, categoryName, finalName)  // ← передаём готовое имя
            }

            currentProductOption?.let { product ->
                 val tempProduct = product.copy(

                     tempFileInfos = fileInfos
                 )
                cartViewModel.addToBasket(tempProduct)
            }
        }
        else
            Toast.makeText(requireContext(), "Выбор отменён", Toast.LENGTH_SHORT).show()
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

        //goToCart = view.findViewById(R.id.goToCart)
       // goToCart.setOnClickListener(){ val intent = Intent (getActivity(), BasketActivity::class.java)
       //   getActivity()?.startActivity(intent)
       // }
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
                currentProductOption = option
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
private fun getOptionsForService(serviceId: Int, imageRes: Int): List<ProductOption> {
    return when (serviceId) {
        1 -> listOf(
            ProductOption(109, imageRes, "А4 чёрно-белая односторонняя", 40),
            ProductOption(110, imageRes,"А4 чёрно-белая двухсторонняя", 70),
            ProductOption(111, imageRes,"А3 чёрно-белая односторонняя",  90),
            ProductOption(112, imageRes,"А3 чёрно-белая двухсторонняя",  150),
            ProductOption(113, imageRes,"А4 цветная односторонняя", 140),
            ProductOption(114, imageRes,"А4 цветная двухсторонняя", 260),
            ProductOption(115, imageRes,"А3 цветная односторонняя",  360),
            ProductOption(116, imageRes,"А3 цветная двухсторонняя",  550)
        )
        2 -> listOf(
            ProductOption(100, imageRes, "7,5х10 (А7) глянцевая бумага", 85),
            ProductOption(101, imageRes, "10х15 (А6) глянцевая бумага", 85),
            ProductOption(102, imageRes,"10х15 (А6) сатиновая бумага", 140),
            ProductOption(103, imageRes,"15х21 (А5) глянцевая бумага",  300),
            ProductOption(104, imageRes,"21х29,7 (А4) глянцевая бумага",  420),
            ProductOption(105, imageRes,"29,7х42 (А3) глянцевая бумага", 720),
            ProductOption(106, imageRes,"42х59,4 (А2) глянцевая бумага", 2240),
            ProductOption(107, imageRes,"59,4х84,1 (А1) глянцевая бумага", 4500),
            ProductOption(108, imageRes,"84,1х118,9 (А0) глянцевая бумага", 9000)


        )
        // Сканирование (id = 6)

        else -> emptyList()
    }
}


