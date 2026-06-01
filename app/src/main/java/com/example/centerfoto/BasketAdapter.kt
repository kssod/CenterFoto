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
        holder.bind(products[position], onQuantityChange)
    }

    override fun getItemCount() = products.size

    class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.basketProductName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cartQuantityTextView)  // ← исправлено
        private val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        private val decreaseButton: Button = itemView.findViewById(R.id.cartDecreaseButton)
        private val increaseButton: Button = itemView.findViewById(R.id.cartIncreaseButton)

        fun bind(product: Product, onQuantityChange: (Product, Int) -> Unit) {
            productName.text = product.name
            quantityTextView.text = product.quantityInBasket.toString()  // ← quantityInBasket
            productPrice.text = "${product.price * product.quantityInBasket} руб."

            increaseButton.setOnClickListener {
                onQuantityChange(product, 1)
            }

            decreaseButton.setOnClickListener {
                onQuantityChange(product, -1)
            }
        }
    }
}