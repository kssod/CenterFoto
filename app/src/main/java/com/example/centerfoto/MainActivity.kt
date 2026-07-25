package com.example.centerfoto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var logoImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var basketButton: ImageButton
    private lateinit var editTextSearching: EditText
    private lateinit var services: MutableList<CardItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        editTextSearching=findViewById(R.id.editTextSearching)
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



         services = mutableListOf(
            CardItem(R.drawable.xerox,1, "Печать документов","Черно-белая и цветная печать документов"),
            CardItem(R.drawable.photoprintcard, 2, "Печать фотографий", "Струйная печать фотографий форматов от А7 до А0"),
            CardItem(R.drawable.scan,3, "Сканирование","Сканирование документов различных форматов, в том числе нестандартных: от А5 до А0+"),

            CardItem(R.drawable.plotter,4, "Печать на плоттере","Печать на инженерной бумаге или ватмане форматов от А4 до А0+"),
            CardItem(R.drawable.doc,5, "Фотографии на документы","Любых размеров по официальным требованиям"),

            CardItem(R.drawable.bcards, 6,"Визитки","Струйные, лазерные, евровизитки, текстурные"),
            CardItem(R.drawable.cup,7,"Печать на кружках","Белые, цветные, кружки-хамелеон, пивные"),
            CardItem(R.drawable.dtf, 8,"DTF печать на одежде","Нанесение изображений на ткань: футболки, кепки, худи"),
            CardItem(R.drawable.pillow, 11,"Фотосувениры","Магнитная бумага, подушки, брелоки - порадуйте близких подарком"),
            CardItem(R.drawable.holst,9, "Холсты","Печать на холсте экосольвентыми чернилами с деревянным подрамником"),
            CardItem(R.drawable.cut, 10,"Плоттерная резка","Резка наклеек, табличек, накатка на ПВХ"),
            CardItem(R.drawable.acsessories,12, "Аксессуары","Компьютерные мыши, наушники, пауэрбэнки, адаптеры, батарейки")

            
        )

        var recyclerListUpdate = listOf<CardItem>()

        var adapter = ServiceAdapter(services) {item ->
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

        editTextSearching.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSearch(adapter)
            }
        })

    }

     private fun updateSearch( adapter:ServiceAdapter) {
         val s = editTextSearching.text.toString()


         val filteredList = if (s.isEmpty()) {
             services}
         else {
             services.filter{it.description.contains(s.toString(),true)
                     ||it.title.contains(s.toString(),true)}.toMutableList()
         }
         adapter.updateList(filteredList)
     }
 }
