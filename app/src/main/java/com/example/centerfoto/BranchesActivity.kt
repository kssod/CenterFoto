package com.example.centerfoto

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView

class BranchesActivity : BaseActivity() {
    private lateinit var komsomoletsNumber: TextView
    private lateinit var universal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branches)

        komsomoletsNumber = findViewById(R.id.komsomolets)
        universal = findViewById(R.id.universal)

        komsomoletsNumber.setOnClickListener {
            val phoneNumber = "+77773304949"

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Не удалось открыть набор номера", Toast.LENGTH_SHORT).show()
            }
        }

        universal.setOnClickListener {
            val phoneNumber = "+77056227735"

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Не удалось открыть набор номера", Toast.LENGTH_SHORT).show()
            }
        }
    }
}