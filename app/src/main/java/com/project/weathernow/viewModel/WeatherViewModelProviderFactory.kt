package com.project.weathernow.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelProviderFactory(
    private val app: Application, private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return modelClass.cast(
                WeatherViewModel(app, weatherRepository)
            )!!
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}