package com.example.project2.repo

import com.example.project2.api.WeatherApi
import com.example.project2.dataClasses.*
import com.example.project2.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call


class WeatherRepository(private val weatherApi: WeatherApi, private val database: AppDatabase) {

    suspend fun getWeatherForecast(apiKey: String, location: String, days: Int, isConnected: Boolean): WeatherResponse? {
        return if (isConnected) {
            val response = withContext(Dispatchers.IO) {
                weatherApi.getWeatherForecast(apiKey, location, days).execute()
            }
            if (response.isSuccessful) {
                response.body()?.also { saveForecastToDatabase(it) }
            } else {
                getLastSavedCityForecast()
            }
        } else {
            getLastSavedCityForecast()
        }
    }

    private suspend fun saveForecastToDatabase(weatherResponse: WeatherResponse) {
        withContext(Dispatchers.IO) {
            val forecasts = weatherResponse.forecast.forecastday.map { forecastDay ->
                ForecastEntity(
                    date = forecastDay.date,
                    avgtemp_c = forecastDay.day.avgtemp_c,
                    maxtemp_c = forecastDay.day.maxtemp_c,
                    mintemp_c = forecastDay.day.mintemp_c,
                    condition_icon = forecastDay.day.condition.icon,
                    hours = forecastDay.hour.map { hour ->
                        Hour(
                            time_epoch = hour.time_epoch,
                            time = hour.time,
                            temp_c = hour.temp_c,
                            is_day = hour.is_day,
                            Condition(
                                text = hour.condition.text,
                                icon = hour.condition.icon
                            ),
                            wind_mph = hour.wind_mph,
                            snow_cm = hour.snow_cm,
                            humidity = hour.humidity,
                            cloud = hour.cloud,
                            feelslike_c = hour.feelslike_c,
                            windchill_c = hour.windchill_c,
                            heatindex_c = hour.heatindex_c,
                            will_it_rain = hour.will_it_rain,
                            chance_of_rain = hour.chance_of_rain,
                            will_it_snow = hour.will_it_snow,
                            chance_of_snow = hour.chance_of_snow
                        )
                    }
                )
            }
            database.forecastDao().insertAllForecasts(forecasts)
        }
    }

    private suspend fun getLastSavedCityForecast(): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            val forecasts = database.forecastDao().getAllForecasts()
            if (forecasts.isNotEmpty()) {
                val forecastDayList = forecasts.map { forecastEntity ->
                    ForecastDay(
                        date = forecastEntity.date,
                        day = Day(
                            avgtemp_c = forecastEntity.avgtemp_c,
                            maxtemp_c = forecastEntity.maxtemp_c,
                            mintemp_c = forecastEntity.mintemp_c,
                            maxwind_mph = 0.0,
                            daily_will_it_rain = 0,
                            daily_chance_of_rain = 0,
                            daily_will_it_snow = 0,
                            daily_chance_of_snow = 0,
                            condition = Condition(
                                text = "",
                                icon = forecastEntity.condition_icon
                            ),
                        ),
                        hour = forecastEntity.hours.map { hourEntity ->
                            Hour(
                                time_epoch = hourEntity.time_epoch,
                                time = hourEntity.time,
                                temp_c = hourEntity.temp_c,
                                is_day = hourEntity.is_day,
                                condition = Condition(
                                    text = "",
                                    icon ="",
                                ),
                                wind_mph = hourEntity.wind_mph,
                                snow_cm = hourEntity.snow_cm,
                                humidity = hourEntity.humidity,
                                cloud = hourEntity.cloud,
                                feelslike_c = hourEntity.feelslike_c,
                                windchill_c = hourEntity.windchill_c,
                                heatindex_c = hourEntity.heatindex_c,
                                will_it_rain = hourEntity.will_it_rain,
                                chance_of_rain = hourEntity.chance_of_rain,
                                will_it_snow = hourEntity.will_it_snow,
                                chance_of_snow = hourEntity.chance_of_snow
                            )
                        }
                    )
                }
                WeatherResponse(
                    location = Location(
                        name = "",
                        country = "",
                        region = "",
                        lat = 0.0,
                        lon = 0.0,
                        localtime = ""),
                    current = Current(
                        temp_c = 0.0,
                        condition = Condition(
                            text = "",
                            icon = ""
                        ),
                        wind_mph = 0.0,
                        humidity = 0,
                        cloud = 0,
                    ),
                    forecast = Forecast(forecastday = forecastDayList)
                )
            } else {
                null
            }
        }
    }
}
