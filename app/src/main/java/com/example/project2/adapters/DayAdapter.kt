package com.example.project2.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project2.dataClasses.Hour
import com.example.project2.databinding.ItemMainTodayBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class DayAdapter(private var hours: List<Hour>) : RecyclerView.Adapter<DayAdapter.HourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding =
            ItemMainTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hour = hours[position]
        holder.bind(hour)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    fun updateData(newHours: List<Hour>) {
        hours = newHours
        notifyDataSetChanged()
    }

    class HourViewHolder(private val binding: ItemMainTodayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hour: Hour) {
            binding.hourDegree.text = "${hour.temp_c}°C"
            binding.time.text = getDateTime(hour.time_epoch)
            Glide.with(binding.root.context)
                .load("https:${hour.condition.icon}")
                .into(binding.icon)
        }

        //        private fun epochToHumanReadable(epoch: Long): String {
//            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault())
//            val formatter = DateTimeFormatter.ofPattern("HH:mm")
//            return dateTime.format(formatter)
//        }
        private fun getDateTime(epochTime: Long): String? {
            try {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val netDate = Date(epochTime * 1000)
                return sdf.format(netDate)
            } catch (e: Exception) {
                return e.toString()
            }
        }
    }
}

