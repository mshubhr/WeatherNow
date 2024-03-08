package com.project.weathernow.ui

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weathernow.MyApplication
import com.project.weathernow.repository.WeatherRepository
import com.project.weathernow.ui.WeatherViewModel

@Suppress("UNCHECKED_CAST")
class WeatherViewModelProviderFactory(val app: Application, val weatherRepository: WeatherRepository) : ViewModelProvider.Factory
{

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(app, weatherRepository) as T
    }

}