package com.example.project2.dataClasses

import android.os.Parcelable

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)
