package com.example.project2.dataClasses

data class Current(
    val temp_c: Double,
    val condition: Condition,
    val wind_mph: Double,
    val humidity: Int,
    val cloud: Int,
)
