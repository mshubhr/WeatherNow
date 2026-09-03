package com.project.weathernow.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.weathernow.models.City
import com.project.weathernow.models.WeatherList

class Converters {

    @TypeConverter
    fun fromWeatherList(weatherList: ArrayList<WeatherList>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(json: String): ArrayList<WeatherList> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<WeatherList>>() {}.type)
    }

    @TypeConverter
    fun fromCity(city: City): String {
        return Gson().toJson(city)
    }

    @TypeConverter
    fun toCity(json: String): City {
        return Gson().fromJson(json, City::class.java)
    }
}