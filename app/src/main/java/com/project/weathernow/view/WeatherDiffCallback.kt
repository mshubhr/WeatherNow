package com.project.weathernow.view

import androidx.recyclerview.widget.DiffUtil
import com.project.weathernow.models.WeatherList

class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherList>() {

    override fun areItemsTheSame(
        oldItem: WeatherList, newItem: WeatherList
    ): Boolean {
        return oldItem.dtTxt == newItem.dtTxt
    }

    override fun areContentsTheSame(
        oldItem: WeatherList, newItem: WeatherList
    ): Boolean {
        return oldItem == newItem
    }
}