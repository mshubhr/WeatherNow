package com.project.weathernow.repository

import com.project.weathernow.models.WeatherList
import com.project.weathernow.api.RetrofitInstance
import com.project.weathernow.db.WeatherDatabase
import com.project.weathernow.models.ForeCast

class WeatherRepository(val db: WeatherDatabase) {

  suspend fun getWeather(city: String) = RetrofitInstance.api.getWeatherByCity(city)

  suspend fun getLatLonWeather(lat: String? = null, lon: String? = null) = RetrofitInstance.api.getCurrentWeather(lat.toString(), lon.toString())

  suspend fun insertWeather(foreCast: ForeCast) = db.weatherListDao().insertWeatherList(foreCast)
  fun getCachedWeather() = db.weatherListDao().getAllWeather()

}