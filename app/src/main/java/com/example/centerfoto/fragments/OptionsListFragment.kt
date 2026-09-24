package com.example.centerfoto.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.centerfoto.dataModel.CartItem
import com.example.centerfoto.CartViewModel
import com.example.centerfoto.MyApp
import com.example.centerfoto.dataModel.ProductOption
import com.example.centerfoto.R
import com.example.centerfoto.activities.BasketActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore

class OptionsListFragment: BottomSheetDialogFragment() {
    init {
        Log.d("OptionsListFragment", "ФРАГМЕНТ СОЗДАН (init)")
    }
    //private lateinit var descriptionText: TextView
    //private lateinit var priceText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var attachButton: ImageButton
    private lateinit var goToCart: Button
    private var currentServiceId: Int = 0
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var goToCartFromOptionList: Button
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


           // ✅ Достаём Set<String> и превращаем обратно в List<Uri>
           val newUriStrings = cartViewModel.addUris(uris)

           val uniqueUris = newUriStrings.map { Uri.parse(it) }
           if (newUriStrings.isEmpty()) {
               Toast.makeText(requireContext(), "Все файлы уже в корзине", Toast.LENGTH_SHORT).show()
               return@registerForActivityResult
           }

           val categoryName: String? = currentProductOption?.name ?: "Без названия"
           Toast.makeText(requireContext(), "Файлы прикреплёны", Toast.LENGTH_SHORT).show()
           val cartItem: List<CartItem> = uniqueUris.map { uri ->
               val originalName = getOriginalFileName(uri) ?: "file_${System.currentTimeMillis()}"

               CartItem(
                   image = currentProductOption?.imageRes,
                   id = System.currentTimeMillis().toInt() + uri.hashCode(),
                   originalFileName = originalName,
                   categoryName = categoryName,
                   pricePerCopy = currentProductOption?.price ?: 100,
                   copies = 1,
                   fileUri = uri,
                   serviceId = currentServiceId
               )
           }
           cartViewModel.addToBasket(cartItem)

       }
        else
            Toast.makeText(requireContext(), "Выбор отменён", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_options_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated вызван")
        cartViewModel = (requireContext().applicationContext as MyApp).cartViewModel
        currentServiceId = arguments?.getInt(ARG_SERVICE_ID) ?: 0
        val imageRes = arguments?.getInt(ARG_IMAGE) ?: 0
        goToCartFromOptionList=view.findViewById(R.id.goToCartFromOptionList)
        progressBar = view.findViewById(R.id.progressBar)
        scrollView = view.findViewById(R.id.scrollView)
        progressBar.visibility = View.VISIBLE
        goToCartFromOptionList.setOnClickListener{ startActivity(Intent(requireContext(),BasketActivity::class.java)) }

        optionsContainer = view.findViewById(R.id.optionsContainer)

        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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

        //val options = getOptionsForService(currentServiceId, imageRes)
        loadOptionsFromFirestore(currentServiceId, imageRes)
    }
    private fun displayOptions(options: List<ProductOption>) {


        progressBar.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
        val context = context
        if (context == null) {
            Log.e(TAG, "Fragment not attached to context")
            return
        }

        Log.d(TAG, "displayOptions вызван, options.size = ${options.size}")

        for (option in options) {
            // ✅ 2. Используем context вместо requireContext()
            val card = LayoutInflater.from(context)
                .inflate(R.layout.option_item_card, optionsContainer, false)


            val descriptionText = card.findViewById<TextView>(R.id.optionDescriptionTextView)
            val priceText = card.findViewById<TextView>(R.id.optionPriceTextView)
            attachButton = card.findViewById(R.id.attachButton)

            descriptionText.text = option.name
            priceText.text = "${option.price} ₸"

            val attachedFiles = {
                currentProductOption = option
                val mimeTypes = when (currentServiceId) {
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
                pickDocsLauncher.launch(mimeTypes)
            }

            attachButton.setOnClickListener { attachedFiles.invoke() }
            card.setOnClickListener { attachedFiles.invoke() }

            optionsContainer.addView(card)
        }
    }
    private fun loadOptionsFromFirestore(serviceId: Int, imageRes: Int) {

        firestore.collection("1")  // ← коллекция называется "1"
            .whereEqualTo("serviceId", serviceId)   // ← фильтруем по serviceId
            .get()
            .addOnSuccessListener { snapshot ->

                // ✅ snapshot — это список документов (QuerySnapshot)
                // ✅ Проходим по каждому документу в коллекции
                for (document in snapshot) {
                    // ✅ document.data — данные КАЖДОГО документа
                    Log.d(TAG, "Документ: ${document.id} => ${document.data}")
                }

                // ✅ Преобразуем список документов в список ProductOption
                val options = snapshot.map { document ->
                    document.toObject(ProductOption::class.java)
                }

                displayOptions(options)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Ошибка загрузки: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

