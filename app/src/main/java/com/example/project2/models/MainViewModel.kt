package com.example.project2.models

import android.util.Log
import androidx.lifecycle.*
import com.example.project2.dataClasses.WeatherResponse
import com.example.project2.repo.WeatherRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> get() = _weatherResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getWeatherForecast(apiKey: String, location: String, days: Int, isConnected: Boolean) {
        viewModelScope.launch {
            try {
                // Use the suspend function from the repository
                val weatherResponse = repository.getWeatherForecast(apiKey, location, days, isConnected)
                if (weatherResponse != null) {
                    _weatherResponse.value = weatherResponse!!
                } else {
                    _errorMessage.value = "No offline data available."
                    Log.e("MainViewModel", "No offline data available.")
                }
            } catch (e: Exception) {
                if (!isConnected) {
                    _errorMessage.value = "No internet connection and no offline data available."
                } else {
                    _errorMessage.value = "Failed to retrieve weather data: ${e.message}"
                }
                Log.e("MainViewModel", "Failed to retrieve weather data", e)
            }
        }
    }
}
