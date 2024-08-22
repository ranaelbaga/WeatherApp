package com.example.project2.dataClasses

data class Hour(
    val time_epoch: Long,
    val time: String,
    val temp_c: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_mph: Double,
    val snow_cm: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val windchill_c: Double,
    val heatindex_c: Double,
    val will_it_rain: Int,
    val chance_of_rain: Int,
    val will_it_snow: Int,
    val chance_of_snow: Int,
)
