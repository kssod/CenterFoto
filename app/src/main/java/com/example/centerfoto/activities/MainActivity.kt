package com.example.centerfoto.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.centerfoto.R
import com.example.centerfoto.adapters.ServiceAdapter
import com.example.centerfoto.dataModel.CardItem
import com.example.centerfoto.fragments.OptionsListFragment
import com.example.centerfoto.fragments.PhotoPrintFragment


class MainActivity : AppCompatActivity() {
    private lateinit var logoImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var basketButton: ImageButton
    private lateinit var editTextSearching: EditText
    private lateinit var services: MutableList<CardItem>
    private lateinit var branchesButton: LinearLayout
    private lateinit var textViewAddress: TextView
    private val REQUEST_CODE_BRANCH = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        branchesButton=findViewById(R.id.branchSelectorButton)
        editTextSearching=findViewById(R.id.editTextSearching)
        logoImageView = findViewById(R.id.logoImageView)
        textViewAddress=findViewById(R.id.textViewAddress)
        basketButton = findViewById(R.id.basketButton)
        basketButton.setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))
        }

        recyclerView = findViewById(R.id.recyclerServices)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    // dy > 0 → скроллим ВНИЗ → прячем кнопку
                    hideBasketButton(basketButton)
                } else if (dy < 0) {
                    // dy < 0 → скроллим ВВЕРХ → показываем кнопку
                    showBasketButton(basketButton)
                }
            }
        })

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        updateBranchButton()

        // Переход в BranchesActivity
        branchesButton.setOnClickListener {
            startActivityForResult(Intent(this, BranchesActivity::class.java), REQUEST_CODE_BRANCH)
        }

         services = mutableListOf(
            CardItem(R.drawable.xerox,1, "Печать документов","Черно-белая и цветная печать документов на офисной бумаге 80гр/м3"),
            CardItem(R.drawable.photoprintcard, 2, "Печать фотографий", "Струйная печать фотографий на глянцевой бумаге различных форматов (10х15, А5, А4 и др.)"),
            CardItem(R.drawable.scan,3, "Сканирование","Сканирование документов различных форматов, в том числе нестандартных: от А5 до А0+"),

            CardItem(R.drawable.plotter,4, "Печать на плоттере","Чертежи, плакаты, постеры на инженерной бумаге форматов от А4 до А0+"),
            CardItem(R.drawable.doc,5, "Фотографии на документы","Любых размеров по официальным требованиям"),

            CardItem(R.drawable.bcards, 6,"Визитные карты","Струйные, лазерные, евровизитки, текстурные"),
            CardItem(R.drawable.cup,7,"Печать на кружках","Белые, цветные, кружки-хамелеон, пивные"),
            CardItem(R.drawable.dtf, 8,"DTF печать на одежде","Нанесение изображений на ткань: футболки, кепки, худи"),
            CardItem(R.drawable.pillow, 11,"Фотосувениры","Магнитная бумага, подушки, брелоки - порадуйте близких подарком"),
            CardItem(R.drawable.holst,9, "Холсты","Печать экосольвентыми чернилами на холсте с деревянным подрамником"),
            CardItem(R.drawable.cut, 10,"Плоттерная резка","Резка наклеек, табличек, накатка на ПВХ"),
            CardItem(R.drawable.acsessories,12, "Аксессуары","Компьютерные мыши, наушники, пауэрбэнки, адаптеры, батарейки")


        )

        var recyclerListUpdate = listOf<CardItem>()

        var adapter = ServiceAdapter(services) {item ->
            when (item.productId) {
                1,2,9,3 -> {
                    val fragment = OptionsListFragment.newInstance(
                        item.productId, item.imageRes)
                    fragment.show(supportFragmentManager, "OptionsList")
                }
               6 -> {
                    val fragment = PhotoPrintFragment.newInstance()
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
    override fun onResume() {
        super.onResume()
        // Обновляем кнопку при возвращении из BranchesActivity
        updateBranchButton()
    }

    private fun updateBranchButton() {
        val branchName = BranchManager.getBranchName(this)
        val branchAddress = BranchManager.getBranchAddress(this)

        textViewAddress.text = if (branchAddress.isNotEmpty()) {
            "$branchName\n$branchAddress"
        } else {
            branchName
        }
    }
     private fun updateSearch( adapter: ServiceAdapter) {
         val s = editTextSearching.text.toString()


         val filteredList = if (s.isEmpty()) {
             services}
         else {
             services.filter{it.description.contains(s.toString(),true)
                     ||it.title.contains(s.toString(),true)}.toMutableList()
         }
         adapter.updateList(filteredList)
     }
    private fun hideBasketButton(button: View) {
        if (button.visibility == View.VISIBLE) {
            button.animate()
                .translationY(button.height + 32f) // сдвигаем вниз за экран
                .alpha(0f)                          // и делаем прозрачной
                .setDuration(250)
                .withEndAction { button.visibility = View.GONE }
                .start()
        }
    }

    private fun showBasketButton(button: View) {
        if (button.visibility != View.VISIBLE) {
            button.visibility = View.VISIBLE
            button.translationY = button.height + 10f // стартуем снизу
            button.alpha = 0f
            button.animate()
                .translationY(0f)   // возвращаем на место
                .alpha(1f)          // проявляем
                .setDuration(250)
                .start()
        }
    }
 }
