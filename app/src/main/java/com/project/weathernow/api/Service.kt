package com.project.weathernow.api

import com.project.weathernow.models.ForeCast
import com.project.weathernow.Utils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    @GET("forecast?")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appID") appID: String = Utils.API_KEY
    ): Call<ForeCast>

    @GET("forecast?")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appID") appID: String = Utils.API_KEY
    ): Call<ForeCast>

}