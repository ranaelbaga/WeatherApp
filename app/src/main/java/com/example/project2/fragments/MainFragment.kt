package com.example.project2.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project2.R
import com.example.project2.adapters.DayAdapter
import com.example.project2.adapters.ForecastAdapter
import com.example.project2.api.RetrofitInstance
import com.example.project2.dataClasses.*
import com.example.project2.databinding.FragmentMainBinding
import com.example.project2.db.AppDatabase
import com.example.project2.models.MainViewModel
import com.example.project2.models.MainViewModelFactory
import com.example.project2.repo.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var viewModel: MainViewModel
    private lateinit var dayAdapter: DayAdapter
    private lateinit var forecastAdapter: ForecastAdapter

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocationAndWeatherData()
        } else {
            fetchWeatherData("Egypt")
            showPermissionDeniedMessage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        val appDatabase = AppDatabase.getDatabase(requireContext())
        val repository = WeatherRepository(
            RetrofitInstance.api,
            appDatabase
        )
        val factory = MainViewModelFactory(repository)

        viewModel = viewModels<MainViewModel> { factory }.value

        dayAdapter = DayAdapter(emptyList())
        forecastAdapter = ForecastAdapter(emptyList())

        binding.rvDay.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDay.adapter = dayAdapter

        binding.rvForcast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvForcast.adapter = forecastAdapter

        binding.saveIcon.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToSavedFragment("Cairo")
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshWeatherData()
        }

        viewModel.weatherResponse.observe(viewLifecycleOwner, Observer { weatherResponse ->
            if (weatherResponse != null) {
                updateUIWithWeatherData(weatherResponse)
            } else {
                handleNoInternet()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                binding.swipeRefreshLayout.isRefreshing = false
                showError(errorMessage)
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkLocationPermission()
    }
    private fun refreshWeatherData() {
        if (lastKnownLocation != null) {
            val isConnected = checkInternetConnection(requireContext())
            viewModel.getWeatherForecast(
                "aa5c10538bcf46b692f140709240208",
                lastKnownLocation!!,
                5,
                isConnected
            )
        } else {
            // If no last known location, fallback to fetching location and weather data
            fetchLocationAndWeatherData()
        }
    }


    private fun updateUIWithWeatherData(weatherResponse: WeatherResponse) {
        val hourlyForecasts = weatherResponse.forecast.forecastday.first().hour.sortedBy { hour ->
            hour.time_epoch
        }
        Log.i("MainFragment", "Hourly Forecasts: $hourlyForecasts")
        dayAdapter.updateData(hourlyForecasts)
        forecastAdapter.updateData(weatherResponse.forecast.forecastday)
        binding.tvLocation.text = weatherResponse.location.region

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM")
        val formattedDate = today.format(formatter)

        binding.tvRegion.text = "Today, $formattedDate"
        binding.tvDegree.text = "${weatherResponse.current.temp_c}°"
        binding.tvHigh.text = "Wind |  ${weatherResponse.current.wind_mph} mph"
        binding.tvLow.text = "Humidity |  ${weatherResponse.current.humidity}%"
        binding.date.text = formattedDate

        Glide.with(binding.root.context)
            .load("https:${weatherResponse.current.condition.icon}")
            .into(binding.icon)
    }

    private fun handleNoInternet() {
        // Show an error message or load data from a local database if available
        Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
        // You can also load data from your local database if you have offline data saved
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchLocationAndWeatherData()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationale()
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionRationale() {
        Toast.makeText(
            requireContext(),
            "Location permission is needed to show weather data for your area.",
            Toast.LENGTH_LONG
        ).show()
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private var lastKnownLocation: String? = null

    private fun fetchLocationAndWeatherData() {
        val isConnected = checkInternetConnection(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val locationQuery = "$latitude,$longitude"
                        lastKnownLocation = locationQuery
                        viewModel.getWeatherForecast(
                            "aa5c10538bcf46b692f140709240208",
                            locationQuery,
                            5,
                            isConnected
                        )
                    } else {
                        fetchWeatherDataOrDefault(isConnected)
                    }
                }.addOnFailureListener {
                    fetchWeatherDataOrDefault(isConnected)
                    showLocationErrorMessage()
                }
            } catch (e: SecurityException) {
                Log.e("MainFragment", "Location permission is not granted.", e)
                fetchWeatherDataOrDefault(isConnected)
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchWeatherDataOrDefault(isConnected: Boolean) {
        if (lastKnownLocation != null) {
            viewModel.getWeatherForecast(
                "aa5c10538bcf46b692f140709240208",
                lastKnownLocation!!,
                5,
                isConnected
            ) }
                else {
            fetchWeatherData("Egypt")
        }
    }

    private fun fetchWeatherData(location: String = "Egypt") {
        val isConnected = checkInternetConnection(requireContext())
        viewModel.getWeatherForecast(
            "aa5c10538bcf46b692f140709240208",
            location,
            5,
            isConnected
        )
    }
    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showLocationErrorMessage() {
        Toast.makeText(
            requireContext(),
            "Failed to get location. Please check your location settings.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            requireContext(),
            "Location permission denied. Showing weather for default location.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
