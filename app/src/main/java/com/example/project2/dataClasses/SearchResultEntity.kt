package com.example.project2.dataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val region: String,
    val country: String

)
