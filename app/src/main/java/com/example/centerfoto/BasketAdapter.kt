package com.example.centerfoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BasketAdapter(
    private val onQuantityChange: (product: Product, change: Int) -> Unit
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    private var products = listOf<Product>()

    class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.basketProductName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cartQuantityTextView)
        private val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        private val decreaseButton: Button = itemView.findViewById(R.id.cartDecreaseButton)
        private val increaseButton: Button = itemView.findViewById(R.id.cartIncreaseButton)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val zipCheck: TextView = itemView.findViewById(R.id.zipCheck)

        fun forBind(product: Product, onQuantityChange: (product: Product, change: Int) -> Unit) {
            productName.text = product.name
            quantityTextView.text = product.quantityInBasket.toString()
            image.setImageResource(product.imageRes)
            if (!product.zipFilePath.isNullOrEmpty()) {
                zipCheck.text = "📎 Файл прикреплен"
                zipCheck.visibility = View.VISIBLE
            } else {
                zipCheck.visibility = View.GONE
            }
            increaseButton.setOnClickListener {
                onQuantityChange(product, 1)
            }
            decreaseButton.setOnClickListener {
                onQuantityChange(product, -1)
            }
        }
    }
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_basket_screen, parent, false)
        return BasketViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.forBind(products[position], onQuantityChange)
    }

    override fun getItemCount() = products.size


}