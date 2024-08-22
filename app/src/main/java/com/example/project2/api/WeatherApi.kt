package com.example.project2.api

import com.example.project2.dataClasses.LocationSearchResponse
import com.example.project2.dataClasses.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int
    ): Call<WeatherResponse>

    @GET("search.json")
    fun searchLocation(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call <List<LocationSearchResponse>>

}