package com.example.centerfoto

import android.app.Application

class MyApp : Application() {
    val cartViewModel by lazy {
        CartViewModel()
    }
}