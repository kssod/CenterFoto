package com.example.centerfoto.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.centerfoto.R
import com.example.centerfoto.dataModel.BranchElement

class BranchesActivity : BaseActivity() {
    private lateinit var branchesContainer: LinearLayout
    private lateinit var branchesActivityEscape: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branches)

        branchesContainer = findViewById(R.id.branchesContainer)
        branchesActivityEscape = findViewById(R.id.branchesActivityEscape)
        branchesActivityEscape.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        val branches: List<BranchElement> = getBranchesList()
        for (branch in branches) {
            val branchCard = LayoutInflater.from(this)
                .inflate(R.layout.card_view_branches_screen, branchesContainer, false)

            val branchName = branchCard.findViewById<TextView>(R.id.branchName)
            val branchAddress = branchCard.findViewById<TextView>(R.id.branchAddress)
            val branchEmail = branchCard.findViewById<TextView>(R.id.branchEmail)

            branchName.text = branch.name
            branchAddress.text = branch.address
            branchEmail.text = branch.email
            branchCard.setOnClickListener {
                BranchManager.saveBranch(
                    this,
                    branch.name,
                    branch.address,
                    branch.phoneNumber,
                    branch.id,
                    )
                finish()
            }

            branchesContainer.addView(branchCard)
        }
    }

    private fun getBranchesList(): List<BranchElement> {
        return listOf(
            BranchElement("«Копицентр»", "пр-т Республики, 30Б", "+77773304949", "copycentr-t@mail.ru",1),
            BranchElement("«Умит»", "мкр. 6, 31 (маг. FixPrice)", "+77059790011","center.ymit@mail.ru", 2),
            BranchElement("«Евразия»", "пр-т Мира, 104", "+77053302525", "center.eurazia@mail.ru",3),
            BranchElement("«ЦУМ»", "пр-т Металлургов, 21", "+77051074040", "center.zum@mail.ru",4),
            BranchElement("«Экспресс»", "пр-т Республики, 20", "+77001417070", "center.zon@mail.ru",5),
            BranchElement("«Одежда»", "пр-т Мира, 94", "+77775607520", "centerfoto7@mail.ru",6),
            BranchElement("«Универсал»","мкр. 9, 3в/г", "+77056227735", "center.di@mail.ru",7),
            BranchElement("«Цех» (для корпоративных клиентов)", "ул. Сейфуллина, 50", "+77057270257", "center-tseh@mail.ru",8)
        )
    }
}