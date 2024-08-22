package com.example.project2.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project2.dataClasses.ForecastEntity

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast")
    fun getAllForecasts(): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecast(forecast: ForecastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllForecasts(forecasts: List<ForecastEntity>)
}