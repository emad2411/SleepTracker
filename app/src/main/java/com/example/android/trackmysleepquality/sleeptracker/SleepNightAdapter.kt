package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter: RecyclerView.Adapter<TextItemViewHolder>() {
    var data=listOf<SleepNight>()
    set(value) {
        field=value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val view=LayoutInflater.from(parent.context)
                .inflate(R.layout.item_text_view,parent,false) as TextView
        return TextItemViewHolder(view)
    }

    override fun getItemCount(): Int =data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.textView.text=data.get(position).toString()
    }

}