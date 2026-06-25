package com.example.centerfoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

 class MainActivity : AppCompatActivity() {
    private lateinit var logoImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var basketButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        logoImageView = findViewById(R.id.logoImageView)
        basketButton = findViewById(R.id.basketButton)
        logoImageView.setOnClickListener {
            startActivity(Intent(this, BranchesActivity::class.java))
        }
        basketButton.setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))
        }
        recyclerView = findViewById(R.id.recyclerServices)

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        val services = listOf(
            CardItem(R.drawable.xerox,1, "Печать документов","Черно-белая и цветная печать документов"),
            CardItem(R.drawable.photoprintcard, 2, "Печать фотографий", "Струйная печать фотографий форматов от А7 до А0"),
            CardItem(R.drawable.scan,3, "Сканирование","Сканирование документов различных форматов, в том числе нестандартных: от А5 до А0+"),

            CardItem(R.drawable.plotter,4, "Печать на плоттере","Печать на инженерной бумаге или ватмане форматов от А4 до А0+"),
            CardItem(R.drawable.doc,5, "Фотографии на документы","Любых размеров по официальным требованиям"),

            CardItem(R.drawable.bcards, 6,"Визитки","Струйные, лазерные, евровизитки, текстурные"),
            CardItem(R.drawable.cup,7,"Печать на кружках","Белые, цветные, кружки-хамелеон, пивные"),
            CardItem(R.drawable.dtf, 8,"DTF печать на одежде","Нанесение изображений, логотипов, надписей на ткань"),
            CardItem(R.drawable.pillow, 11,"Фотосувениры","Магнитная бумага, подушки, брелоки - порадуйте близких подарком"),
            CardItem(R.drawable.holst,9, "Холсты","Печать на холсте экосольвентыми чернилами с деревянным подрамником"),
            CardItem(R.drawable.cut, 10,"Плоттерная резка","Резка наклеек, табличек, накатка на ПВХ"),
            CardItem(R.drawable.acsessories,12, "Аксессуары","Компьютерные мыши, наушники, пауэрбэнки, адаптеры, батарейки")

            
        )
        val adapter = ServiceAdapter(services) {item ->
            when (item.productId) {
                1,2 -> {
                    val fragment = OptionsListFragment.newInstance(
                        item.productId, item.imageRes)
                    fragment.show(supportFragmentManager, "OptionsList")
                }
                else -> {
                    val fragment = PhotoPrintFragment.newInstance(
                        item.productId, item.title, item.description, item.imageRes
                    )
                    fragment.show(supportFragmentManager, "ServiceDetail")
                }
            }
        }
        recyclerView.adapter = adapter
    }
}
