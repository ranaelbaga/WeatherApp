package com.example.project2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project2.R
import com.example.project2.dataClasses.SavedLocation
import com.example.project2.dataClasses.WeatherResponse

class SavedAdapter(
    private var savedLocations: List<SavedLocation>,
    private var weatherDataMap: Map<SavedLocation, WeatherResponse>,
    private val onItemClick: (SavedLocation) -> Unit
) : RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved, parent, false)
        return SavedViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val savedLocation = savedLocations[position]
        val weatherData = weatherDataMap[savedLocation]
        holder.bind(savedLocation, weatherData)
    }

    override fun getItemCount(): Int = savedLocations.size

    fun getSavedLocationAt(position: Int): SavedLocation? {
        return if (position in savedLocations.indices) savedLocations[position] else null
    }

    fun updateData(newSavedLocations: List<SavedLocation>, newWeatherDataMap: Map<SavedLocation, WeatherResponse>) {
        savedLocations = newSavedLocations
        weatherDataMap = newWeatherDataMap
        notifyDataSetChanged()
    }

    class SavedViewHolder(
        itemView: View,
        private val onItemClick: (SavedLocation) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val regionTextView: TextView = itemView.findViewById(R.id.tvRegion)
        private val degreeTextView: TextView = itemView.findViewById(R.id.tvDegree)
        private val highTextView: TextView = itemView.findViewById(R.id.tvHigh)
        private val lowTextView: TextView = itemView.findViewById(R.id.tvLow)
        private val iconImageView: ImageView = itemView.findViewById(R.id.icon)

        fun bind(savedLocation: SavedLocation, weatherData: WeatherResponse?) {
            regionTextView.text = savedLocation.name
            if (weatherData != null) {
                degreeTextView.text = "${weatherData.current.temp_c}°"
                highTextView.text = "H:${weatherData.forecast.forecastday[0].day.maxtemp_c}°"
                lowTextView.text = "L:${weatherData.forecast.forecastday[0].day.mintemp_c}°"
            }

            if (weatherData != null) {
                Glide.with(itemView.context)
                    .load("https:${weatherData.current.condition.icon}")
                    .into(iconImageView)
            }

            itemView.setOnClickListener {
                onItemClick(savedLocation)
            }
        }
    }
}
