package com.umc.yourweather.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.umc.yourweather.R

class DisplayTextAdapter(private var inputText: String?):
    RecyclerView.Adapter<DisplayTextAdapter.DisplayTextViewHolder>() {

    class DisplayTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.recyclerview_calendar_detailview)

    }

    // 1. Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisplayTextViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_calendar_detailview, parent, false)

        return DisplayTextViewHolder(cardView)
    }

    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: DisplayTextViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.textView.text = inputText ?: "No input text"
    }

    // 3. Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return 5
    }
    fun updateText(newText: String) {
        inputText=newText
        notifyDataSetChanged()
    }
}