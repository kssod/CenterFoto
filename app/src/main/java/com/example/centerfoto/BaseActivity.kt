package com.example.centerfoto

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val rootView =
            findViewById<View>(android.R.id.content)           // или R.id.main_container / root_layout
        // или конкретный layout: findViewById<ConstraintLayout>(R.id.root)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,           // ← вот это отодвинет всё вниз от статус-бара
                systemBars.right,
                systemBars.bottom
            )

            // Важно: возвращаем insets дальше (особенно если есть вложенные scrollview/recyclerview)
            insets
        }
    }
}