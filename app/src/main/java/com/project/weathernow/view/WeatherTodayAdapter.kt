package com.project.weathernow.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.weathernow.R
import com.project.weathernow.databinding.ItemWeatherCardBinding
import com.project.weathernow.models.WeatherList

class WeatherToday : ListAdapter<WeatherList, WeatherToday.TodayHolder>(WeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayHolder {
        return TodayHolder(
            ItemWeatherCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TodayHolder, position: Int) {
        holder.bind(getItem(position), if (position > 0) getItem(position - 1) else null)
    }

    class TodayHolder(
        private val binding: ItemWeatherCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            todayForecast: WeatherList, previousForecast: WeatherList?
        ) {
            val context = itemView.context

            if (previousForecast != null) {
                binding.speedIndicator.rotation =
                    if ((todayForecast.wind?.speed ?: 0.0) < (previousForecast.wind?.speed
                            ?: 0.0)
                    ) {
                        145F
                    } else {
                        0F
                    }
            } else {
                binding.speedIndicator.rotation = 0F
            }

            binding.weatherTime.text = todayForecast.dtTxt?.substring(11, 16) ?: ""

            binding.weatherTemp.text = context.getString(
                R.string.temp_format, todayForecast.main?.temp?.minus(273.15) ?: 0.0
            )

            binding.weatherSpeed.text = context.getString(
                R.string.speed_format, todayForecast.wind?.speed ?: 0.0
            )

            Glide.with(context).load(
                when (todayForecast.weather.firstOrNull()?.icon) {
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
            ).into(binding.weatherIcon)
        }

    }
}