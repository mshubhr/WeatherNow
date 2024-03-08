package com.project.weathernow.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.project.weathernow.models.City
import com.project.weathernow.models.WeatherList

@Entity(tableName = "forecast_list")

data class ForeCast(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @SerializedName("cod") var cod: String? = null,
  @SerializedName("message") var message: Int? = null,
  @SerializedName("cnt") var cnt: Int? = null,
  @SerializedName("list") var weatherList: ArrayList<WeatherList> = arrayListOf(),
  @SerializedName("city") var city: City? = City()
)