package com.example.project2.db

import androidx.room.TypeConverter
import com.example.project2.dataClasses.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromLocation(location: Location): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        val type = object : TypeToken<Location>() {}.type
        return gson.fromJson(locationString, type)
    }

    @TypeConverter
    fun fromCurrent(current: Current): String {
        return gson.toJson(current)
    }

    @TypeConverter
    fun toCurrent(currentString: String): Current {
        val type = object : TypeToken<Current>() {}.type
        return gson.fromJson(currentString, type)
    }

    @TypeConverter
    fun fromForecast(forecast: Forecast): String {
        return gson.toJson(forecast)
    }

    @TypeConverter
    fun toForecast(forecastString: String): Forecast {
        val type = object : TypeToken<Forecast>() {}.type
        return gson.fromJson(forecastString, type)
    }

    @TypeConverter
    fun fromForecastDay(forecastDay: ForecastDay): String {
        return gson.toJson(forecastDay)
    }

    @TypeConverter
    fun toForecastDay(forecastDayString: String): ForecastDay {
        val type = object : TypeToken<ForecastDay>() {}.type
        return gson.fromJson(forecastDayString, type)
    }

    @TypeConverter
    fun fromHourList(hourList: List<Hour>): String {
        return gson.toJson(hourList)
    }

    @TypeConverter
    fun toHourList(hourListString: String): List<Hour> {
        val type = object : TypeToken<List<Hour>>() {}.type
        return gson.fromJson(hourListString, type)
    }

    @TypeConverter
    fun fromDay(day: Day): String {
        return gson.toJson(day)
    }

    @TypeConverter
    fun toDay(dayString: String): Day {
        val type = object : TypeToken<Day>() {}.type
        return gson.fromJson(dayString, type)
    }

    @TypeConverter
    fun fromCondition(condition: Condition): String {
        return gson.toJson(condition)
    }

    @TypeConverter
    fun toCondition(conditionString: String): Condition {
        val type = object : TypeToken<Condition>() {}.type
        return gson.fromJson(conditionString, type)
    }

    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String): WeatherResponse {
        val type = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(weatherResponseString, type)
    }
}
