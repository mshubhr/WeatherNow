package com.project.weathernow.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.weathernow.MyApplication
import com.project.weathernow.models.ForeCast
import com.project.weathernow.models.WeatherList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class WeatherViewModel(app: Application, val weatherRepository: WeatherRepository) : AndroidViewModel(app) {

    val todayWeatherLiveData = MutableLiveData<List<WeatherList>>()
    val weatherLiveData = MutableLiveData<WeatherList?>()
    val cityName = MutableLiveData<String?>()
    val showToast = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val forecast: List<ForeCast> = weatherRepository.getCachedWeather()

            if (forecast.isNotEmpty()) {
                val todayWeatherList = mutableListOf<WeatherList>()
                cityName.postValue(forecast[forecast.size - 1].city!!.name)

                forecast[forecast.size - 1].weatherList.forEach { weather ->
                    if (weather.dtTxt!!.split("\\s".toRegex()).contains(
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        )
                    ) {
                        todayWeatherList.add(weather)
                    }
                }

                weatherLiveData.postValue(findClosestWeather(todayWeatherList))
                todayWeatherLiveData.postValue(todayWeatherList)
            }
        }
    }

    fun getWeather(city: String? = null, lat: Double? = null, lon: Double? = null) =
        viewModelScope.launch(Dispatchers.IO) {
            val todayWeatherList = mutableListOf<WeatherList>()

            if (hasInternetConnection()) {
                val response = if (city != null) {
                    weatherRepository.getWeather(city)
                } else {
                    weatherRepository.getLatLonWeather(lat.toString(), lon.toString())
                }.execute()

                if (response.isSuccessful) {
                    cityName.postValue(response.body()?.city!!.name)

                    response.body()?.weatherList?.forEach { weather ->
                        if (weather.dtTxt!!.split("\\s".toRegex()).contains(
                                SimpleDateFormat(
                                    "yyyy-MM-dd", Locale.getDefault()
                                ).format(Date())
                            )
                        ) todayWeatherList.add(weather)
                    }

                    weatherLiveData.postValue(findClosestWeather(todayWeatherList))
                    todayWeatherLiveData.postValue(todayWeatherList)
                    viewModelScope.launch { weatherRepository.insertWeather(response.body()!!) }

                } else {
                    Log.e("CurrentWeatherError", "Error: ${response.message()}")
                }
            } else {
                showToast.postValue(true)
            }
        }

    private fun findClosestWeather(weatherList: List<WeatherList>): WeatherList? {
        var closestWeather: WeatherList? = null
        var minTimeDifference = Int.MAX_VALUE

        for (weather in weatherList) {
            val timeDifference = abs(
                timeToMinutes(
                    weather.dtTxt!!.substring(11, 16)
                ) - timeToMinutes(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
            )

            if (timeDifference < minTimeDifference) {
                minTimeDifference = timeDifference
                closestWeather = weather
            }
        }

        return closestWeather
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork ?: return false
        ) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun timeToMinutes(time: String): Int {
        return time.split(":")[0].toInt() * 60 + time.split(":")[1].toInt()
    }

}