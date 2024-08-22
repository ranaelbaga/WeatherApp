package com.example.project2.dataClasses

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>

)
