package com.example.project2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project2.R
import com.example.project2.dataClasses.ForecastDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class ForecastAdapter(private var forecastList: List<ForecastDay>) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvg: TextView = itemView.findViewById(R.id.tvavg)
        val tvDay: TextView = itemView.findViewById(R.id.day)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_forcast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = forecastList[position]
        holder.tvAvg.text = "${item.day.avgtemp_c}°C"

        val date = LocalDate.parse(item.date, DateTimeFormatter.ISO_DATE)
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        holder.tvDay.text = dayOfWeek

        Glide.with(holder.itemView.context)
            .load("https:${item.day.condition.icon}")
            .into(holder.icon)
    }

    override fun getItemCount(): Int = forecastList.size

    fun updateData(newForecastList: List<ForecastDay>) {
        forecastList = newForecastList
        notifyDataSetChanged()
    }
}
