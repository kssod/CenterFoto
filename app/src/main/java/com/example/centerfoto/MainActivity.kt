package com.example.centerfoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

 class MainActivity : BaseActivity() {
    private lateinit var logoImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var basketButton: Button
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
            CardItem(R.drawable.photoprintcard, 1,"Печать фотографий", "Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.dtf, 2,"DTF печать на одежде","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.cup,3,"Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.plotter,4, "Широкоформатная печать","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.photoprintcard, 5,"Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.dtf,6, "Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.cup,7, "Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.plotter, 8,"Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.photoprintcard,9, "Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.dtf, 10,"Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.cup,11, "Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань"),
            CardItem(R.drawable.plotter,12, "Печать на кружках","Печать на футболках — это процесс нанесения изображений, логотипов, надписей или фотографий на ткань")
        )
        val adapter: ServiceAdapter = ServiceAdapter(services) { item -> val fragment = PhotoPrintFragment.newInstance(productId = item.productId, title = item.title, description = item.description); fragment.show(supportFragmentManager, "ServiceDetail") }
        recyclerView.adapter = adapter

    }
}