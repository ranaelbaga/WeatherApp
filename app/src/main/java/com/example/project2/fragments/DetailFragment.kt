package com.example.project2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project2.R
import com.example.project2.adapters.DayAdapter
import com.example.project2.adapters.ForecastAdapter
import com.example.project2.dataClasses.WeatherResponse
import com.example.project2.databinding.FragmentDetailBinding
import com.example.project2.models.DetailViewModel
import com.example.project2.models.DetailViewModelFactory
import com.example.project2.db.SavedDatabase
import com.example.project2.repo.SavedLocationRepository
import kotlinx.coroutines.launch
import com.example.project2.dataClasses.Current
import com.example.project2.dataClasses.Condition
import com.example.project2.dataClasses.Forecast
import com.example.project2.dataClasses.Location

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var dayAdapter: DayAdapter
    private lateinit var forecastAdapter: ForecastAdapter
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels {
        val database = SavedDatabase.getDatabase(requireContext())
        val repository = SavedLocationRepository(database)
        DetailViewModelFactory(repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailBinding.bind(view)

        // Initialize the adapters with empty data
        dayAdapter = DayAdapter(emptyList())
        forecastAdapter = ForecastAdapter(emptyList())

        val cityName = args.cityName
        binding.tvLocation.text = cityName

        binding.rvDay.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDay.adapter = dayAdapter

        binding.rvForcast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvForcast.adapter = forecastAdapter
        binding.back.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToSavedFragment("Cairo")
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            val savedLocation = viewModel.getSavedLocationByName(cityName)
            if (savedLocation != null) {
                val weatherData = viewModel.fetchWeatherData(savedLocation)
                if (weatherData != null) {
                    displayWeatherData(weatherData)
                }
            }
        }
    }

    private fun displayWeatherData(weatherData: WeatherResponse) {
        binding.tvRegion.text = weatherData.location.region
        binding.tvDegree.text = "${weatherData.current.temp_c}°"
        binding.tvHigh.text = "Wind: ${weatherData.current.wind_mph} mph"
        binding.tvLow.text = "Humidity: ${weatherData.current.humidity}%"


        val hourlyForecasts = weatherData.forecast.forecastday.flatMap { it.hour }


        dayAdapter.updateData(hourlyForecasts)
        forecastAdapter.updateData(weatherData.forecast.forecastday)

        Glide.with(this)
            .load("https:${weatherData.current.condition.icon}")
            .into(binding.icon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
