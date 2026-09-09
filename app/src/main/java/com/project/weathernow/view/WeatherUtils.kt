package com.project.weathernow.view

import com.project.weathernow.R

object WeatherUtils {
    fun getWeatherIcon(iconCode: String?): Int {
        return when (iconCode) {
            "01d" -> R.drawable.oned
            "01n" -> R.drawable.onen
            "02d" -> R.drawable.twod
            "02n" -> R.drawable.twon
            "03d", "03n" -> R.drawable.threedn
            "04d", "04n" -> R.drawable.fourdn
            "09d", "09n" -> R.drawable.ninedn
            "10d" -> R.drawable.tend
            "10n" -> R.drawable.tenn
            "11d", "11n" -> R.drawable.elevend
            "13d", "13n" -> R.drawable.thirteend
            "50d", "50n" -> R.drawable.fiftydn
            else -> R.drawable.oned
        }
    }
}