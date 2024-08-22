package com.example.project2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project2.R
import com.example.project2.adapters.SavedAdapter
import com.example.project2.dataClasses.Location
import com.example.project2.dataClasses.SavedLocation
import com.example.project2.dataClasses.WeatherResponse
import com.example.project2.databinding.FragmentSavedBinding
import com.example.project2.db.SavedDatabase
import com.example.project2.models.SavedViewModel
import com.example.project2.models.SavedViewModelFactory
import com.example.project2.repo.SavedLocationRepository
import kotlinx.coroutines.launch
import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class SavedFragment : Fragment(R.layout.fragment_saved) {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SavedAdapter

    private val args: SavedFragmentArgs by navArgs()
    private lateinit var viewModel: SavedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSavedBinding.bind(view)

        val database = SavedDatabase.getDatabase(requireContext())
        val repository = SavedLocationRepository(database)
        val factory = SavedViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SavedViewModel::class.java)

        val selectedCity = args.selectedCity

        if (selectedCity.isNotEmpty()) {
            val location = Location(
                name = selectedCity,
                region = "dummy_region",
                country = "dummy_country",
                lat = 0.0,
                lon = 0.0,
                localtime = "dummy_time"
            )
            viewModel.saveLocation(location)
        }

        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = SavedAdapter(emptyList(), emptyMap()) { selectedLocation ->
            val action = SavedFragmentDirections.actionSavedFragmentToDetailFragment(
                cityName = selectedLocation.name,
                region = selectedLocation.region,
                country = selectedLocation.country,
                latitude = selectedLocation.lat.toFloat(),
                longitude = selectedLocation.lon.toFloat(),
                localtime = selectedLocation.localtime
            )
            findNavController().navigate(action)
        }
        binding.rv.adapter = adapter

        viewModel.getSavedLocations().observe(viewLifecycleOwner) { savedLocations ->
            lifecycleScope.launch {
                val weatherDataMap = fetchWeatherData(savedLocations)
                adapter.updateData(savedLocations, weatherDataMap)
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val savedLocation = adapter.getSavedLocationAt(position)
                savedLocation?.let {
                    showDeleteConfirmationDialog(it, position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)

        binding.imageView2.setOnClickListener {
            findNavController().navigate(R.id.action_savedFragment_to_searchFragment)
        }
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_savedFragment_to_mainFragment)
        }
    }

    private suspend fun fetchWeatherData(savedLocations: List<SavedLocation>): Map<SavedLocation, WeatherResponse> {
        val weatherDataMap = mutableMapOf<SavedLocation, WeatherResponse>()
        for (location in savedLocations) {
            val weatherResponse = viewModel.fetchWeatherData(location)
            if (weatherResponse != null) {
                weatherDataMap[location] = weatherResponse
            }
        }
        return weatherDataMap
    }

    private fun showDeleteConfirmationDialog(savedLocation: SavedLocation, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Location")
            .setMessage("Are you sure you want to delete this location?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    savedLocation.id?.let {
                        viewModel.deleteLocation(it) // Delete from the database
                        adapter.updateData(viewModel.getSavedLocations().value ?: emptyList(), mapOf()) // Refresh adapter with updated data
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                adapter.notifyItemChanged(position) // Reset swipe
                dialog.dismiss()
            }
            .create()
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
