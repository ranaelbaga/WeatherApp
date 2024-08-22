package com.example.project2.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project2.api.RetrofitInstance
import com.example.project2.dataClasses.LocationSearchResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val _locationSearchResponse = MutableLiveData<List<LocationSearchResponse>?>()
    val locationSearchResponse: LiveData<List<LocationSearchResponse>?> get() = _locationSearchResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    private val _selectedCity = MutableLiveData<String>()
    val selectedCity: LiveData<String> get() = _selectedCity

    fun searchLocation(apiKey: String, query: String) {
        viewModelScope.launch {
            val call = RetrofitInstance.api.searchLocation(apiKey, query)
            call.enqueue(object : Callback<List<LocationSearchResponse>> {
                override fun onResponse(
                    call: Call<List<LocationSearchResponse>>,
                    response: Response<List<LocationSearchResponse>>
                ) {
                    if (response.isSuccessful) {
                        val locations = response.body()
                        if (locations != null) {
                            _locationSearchResponse.value = locations
                            Log.d("SearchViewModel", "Fetched locations: $locations")
                        } else {
                            Log.d("SearchViewModel", "Response body is null")
                        }
                    } else {
                        Log.d("SearchViewModel", "Response error: ${response.errorBody()?.string()}")
                        _errorMessage.value = response.message()
                    }
                }

                override fun onFailure(call: Call<List<LocationSearchResponse>>, t: Throwable) {
                    Log.d("SearchViewModel", "API call failed: ${t.message}")
                    _errorMessage.value = t.message
                }
            })
        }
    }

    fun setSelectedCity(city: String) {
        _selectedCity.value = city
    }
}