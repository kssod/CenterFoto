package com.example.centerfoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BasketActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var adapter: BasketAdapter
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basket_activity)
        cartViewModel = (application as MyApp).cartViewModel

        cartRecyclerView = findViewById(R.id.basketRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        setupRecyclerView()
        setupObservers()


    }

    private fun setupRecyclerView() {
        adapter = BasketAdapter() { product, change ->
            when (change) {
                1 -> cartViewModel.addToBasket(product)
                -1 -> cartViewModel.removeFromBasket(product)}
        }


        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Наблюдаем за списком товаров
        cartViewModel._cartItems.observe(this) { cartList ->
            adapter.updateProducts(cartList)


        }
    }
}