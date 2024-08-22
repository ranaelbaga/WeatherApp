package com.example.project2.dataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
data class SavedLocation(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val country: String,
    val region: String,
    val lat: Double,
    val lon: Double,
    val localtime: String
)
