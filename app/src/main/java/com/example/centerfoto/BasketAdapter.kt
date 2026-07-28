package com.example.centerfoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BasketAdapter(
    private val onQuantityChange: (productOption: ProductOption, change: Int) -> Unit
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    private var productOptions = listOf<ProductOption>()

    class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.basketProductName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cartQuantityTextView)
        private val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        private val decreaseButton: Button = itemView.findViewById(R.id.cartDecreaseButton)
        private val increaseButton: Button = itemView.findViewById(R.id.cartIncreaseButton)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val zipCheck: TextView = itemView.findViewById(R.id.zipCheck)

        fun forBind(productOption: ProductOption, onQuantityChange: (productOption: ProductOption, change: Int) -> Unit) {
            productName.text = productOption.name
            quantityTextView.text = productOption.quantityInBasket.toString()
            image.setImageResource(productOption.imageRes)
            if (!productOption.zipFilePath.isNullOrEmpty()) {
                zipCheck.text = "📎 Файл прикреплен"
                zipCheck.visibility = View.VISIBLE
            } else {
                zipCheck.visibility = View.GONE
            }
            increaseButton.setOnClickListener {
                onQuantityChange(productOption, 1)
            }
            decreaseButton.setOnClickListener {
                onQuantityChange(productOption, -1)
            }
        }
    }
    fun updateProducts(newProductOptions: List<ProductOption>) {
        productOptions = newProductOptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_basket_screen, parent, false)
        return BasketViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.forBind(productOptions[position], onQuantityChange)
    }

    override fun getItemCount() = productOptions.size


}