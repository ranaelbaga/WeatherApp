package com.example.project2.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project2.api.RetrofitInstance
import com.example.project2.dataClasses.Location
import com.example.project2.dataClasses.SavedLocation
import com.example.project2.dataClasses.WeatherResponse
import com.example.project2.repo.SavedLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val savedLocationRepository: SavedLocationRepository): ViewModel() {
    fun saveLocation(location: Location) = viewModelScope.launch {
        val savedLocation = SavedLocation(
            name = location.name,
            region = location.region,
            country = location.country,
            lat = location.lat,
            lon = location.lon,
            localtime = location.localtime
        )
        savedLocationRepository.upsert(savedLocation)
    }

    fun getSavedLocations() = savedLocationRepository.getSavedLocation()

    fun deleteLocation(locationId: Int) = viewModelScope.launch {
        savedLocationRepository.deleteLocationById(locationId)
    }
    suspend fun getSavedLocationByName(name: String): SavedLocation? {
        return savedLocationRepository.getSavedLocationByName(name)
    }


    suspend fun fetchWeatherData(location: SavedLocation): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {

                RetrofitInstance.api.getWeatherForecast(
                    apiKey = "aa5c10538bcf46b692f140709240208",
                    location = location.name,
                    days = 5
                ).execute().body()
            } catch (e: Exception) {

                null
            }
        }
    }
}
