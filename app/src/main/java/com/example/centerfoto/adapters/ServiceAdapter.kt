package com.example.centerfoto.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.centerfoto.dataModel.CardItem
import com.example.centerfoto.R

class ServiceAdapter(
    private var items: MutableList<CardItem>,
    private val onItemClick: (CardItem) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {
    fun updateList(newItems: MutableList<CardItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.cardImage)
        val title: TextView = itemView.findViewById(R.id.cardTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_main_screen, parent, false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CardItem = items[position]
        holder.image.setImageResource(item.imageRes)
        holder.title.text = item.title


        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        holder.itemView.setOnLongClickListener {
            showDescriptionDialog(holder.itemView.context, item)
            true
        }

    }
    private fun showDescriptionDialog(context: Context, item: CardItem) {
        AlertDialog.Builder(context)
            .setTitle(item.title)
            .setMessage(item.description)
            .setPositiveButton("Закрыть", null)
            .show()
    }

    override fun getItemCount(): Int = items.size
}