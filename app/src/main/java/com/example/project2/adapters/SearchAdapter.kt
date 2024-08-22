package com.example.project2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project2.dataClasses.LocationSearchResponse
import com.example.project2.databinding.ItemSearchBinding

class SearchAdapter(
    private var regions: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
    }

    override fun getItemCount(): Int = regions.size

    fun updateData(newRegions: List<String>) {
        regions = newRegions
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(region: String) {
            binding.tvSearchLocation.text = region
            binding.root.setOnClickListener {
                onItemClick(region)
            }
        }
    }
}