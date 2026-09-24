package com.example.centerfoto

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    val cartViewModel by lazy {
        CartViewModel()
    }
    override fun onCreate() {
        super.onCreate()
        // ✅ Инициализация Firebase
        FirebaseApp.initializeApp(this)
    }
}