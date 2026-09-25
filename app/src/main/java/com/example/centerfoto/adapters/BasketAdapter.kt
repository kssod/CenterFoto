package com.example.centerfoto.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.centerfoto.dataModel.CartItem
import com.example.centerfoto.R
import com.google.android.material.textfield.TextInputEditText

class BasketAdapter(
    private val onQuantityChange: (cartItem: CartItem, Int) -> Unit,
    private val onPageRangeChange: (cartItem: CartItem, String) -> Unit,
    private val onFileClick: (CartItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<CartItem> = emptyList()

    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item.serviceId == 1) THE_FIRST_VIEW else THE_SECOND_VIEW
    }

    class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.basketProductName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cartQuantityTextView)
        private val productPrice: TextView = itemView.findViewById(R.id.priceForItem)
        private val decreaseButton: ImageView = itemView.findViewById(R.id.cartDecreaseButton)
        private val increaseButton: ImageView = itemView.findViewById(R.id.cartIncreaseButton)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val exploreButton:ImageView = itemView.findViewById(R.id.exploreButton)
        fun forBind(
            cartItem: CartItem,
            onQuantityChange: (CartItem, Int) -> Unit,
            onFileClick: (CartItem) -> Unit
        ) {
            categoryName.text = cartItem.categoryName
            productPrice.text = "${cartItem.pricePerCopy} ₸"
            productName.text = cartItem.originalFileName
            quantityTextView.text = cartItem.copies.toString()

            if (cartItem.isImage()) {
                cartItem.fileUri?.let { uri ->
                    Glide.with(itemView.context)
                        .load(uri)
                        .override(80, 128)
                        .centerCrop()
                        .into(image)
                }
            } else {
                image.setImageResource(cartItem.image!!)
            }
            exploreButton.setOnClickListener {
                onFileClick(cartItem)
            }
            increaseButton.setOnClickListener {
                onQuantityChange(cartItem, 1)
            }
            decreaseButton.setOnClickListener {
                onQuantityChange(cartItem, -1)
            }
        }
    }

    class BasketViewHolderWithRange(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.basketProductName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cartQuantityTextView)
        private val productPrice: TextView = itemView.findViewById(R.id.priceForItem)
        private val decreaseButton: ImageView = itemView.findViewById(R.id.cartDecreaseButton)
        private val increaseButton: ImageView = itemView.findViewById(R.id.cartIncreaseButton)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val pagesRange: TextInputEditText = itemView.findViewById(R.id.pagesRange)
        private val exploreButton:ImageView = itemView.findViewById(R.id.exploreButton)
        private var textWatcher: TextWatcher? = null  // ← храним ссылку
        fun forBind(
            cartItem: CartItem,
            onQuantityChange: (CartItem, Int) -> Unit,
            onPageRangeChange: (CartItem, String) -> Unit,
            onFileClick: (CartItem) -> Unit
        ) {
            categoryName.text = cartItem.categoryName
            productPrice.text = "${cartItem.pricePerCopy} ₸"
            productName.text = cartItem.originalFileName
            quantityTextView.text = cartItem.copies.toString()

            if (cartItem.isImage()) {
                cartItem.fileUri?.let { uri ->
                    Glide.with(itemView.context)
                        .load(uri)
                        .override(80, 128)
                        .centerCrop()
                        .into(image)
                }
            } else {
                image.setImageResource(cartItem.image!!)
            }

            increaseButton.setOnClickListener {
                onQuantityChange(cartItem, 1)
            }
            decreaseButton.setOnClickListener {
                onQuantityChange(cartItem, -1)
            }
            // работа с рецайклером при вводе в edittext и потере фокуса
            //  Удаляем старый слушатель
            textWatcher?.let { pagesRange.removeTextChangedListener(it) }

            // Устанавливаем текст из текущего объекта
            if (pagesRange.text.toString() != cartItem.pageRange) {
                pagesRange.setText(cartItem.pageRange)
            }

            // Создаём новый слушатель для текущего объекта
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    cartItem.pageRange = s.toString()  // ← сохраняем в ТЕКУЩИЙ объект
                }
            }

            //Добавляем новый слушатель
            pagesRange.addTextChangedListener(textWatcher)
            exploreButton.setOnClickListener {
                onFileClick(cartItem)
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == THE_FIRST_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_cart_screen_with_pages, parent, false)
            return BasketViewHolderWithRange(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_basket_screen, parent, false)
        return BasketViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is BasketViewHolder -> {
                holder.forBind(item, onQuantityChange,onFileClick)
            }

            is BasketViewHolderWithRange -> {
                holder.forBind(item, onQuantityChange, onPageRangeChange,onFileClick)
            }
        }
    }


    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
