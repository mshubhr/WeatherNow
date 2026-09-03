package com.project.weathernow.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.weathernow.R
import com.project.weathernow.databinding.ActivityMainBinding
import com.project.weathernow.db.WeatherDatabase
import com.project.weathernow.models.WeatherList
import com.project.weathernow.viewModel.WeatherRepository
import com.project.weathernow.viewModel.WeatherViewModel
import com.project.weathernow.viewModel.WeatherViewModelProviderFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: WeatherViewModel
    lateinit var adapter: WeatherToday
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val newsRepository = WeatherRepository(WeatherDatabase.Companion(this))
        val viewModelProviderFactory = WeatherViewModelProviderFactory(application, newsRepository)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)
        adapter = WeatherToday()
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.searchCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val enteredText = binding.searchCity.text.toString()
                viewModel.getWeather(enteredText)
                true
            } else {
                false
            }
        }

        viewModel.cityName.observe(this, Observer {
            binding.layoutWeather.weatherCity.text = it
        })

        viewModel.weatherLiveData.observe(this, Observer {
            binding.parentView.visibility = View.VISIBLE
            val temperatureCelsius = it!!.main?.temp?.minus(273.15) ?: 0.0

            binding.layoutWeather.weatherCity.text

            for (i in it.weather) {
                binding.layoutWeather.weatherType.text = i.description
            }

            binding.layoutWeather.weatherTemp.text =
                getString(R.string.temp_format, temperatureCelsius)

            binding.layoutWeather.weatherHumidity.text =
                getString(R.string.humidity_format, it.main?.humidity ?: 0)
            binding.layoutWeather.weatherWind.text =
                getString(R.string.speed_format, it.wind?.speed ?: 0.0)
            binding.layoutWeather.weatherRain.text =
                getString(R.string.rain_format, it.clouds?.all ?: 0)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dtTxt!!)
            val outputFormat = SimpleDateFormat("d MMMM EEEE,  HH:mm", Locale.getDefault())
            val dateanddayname = outputFormat.format(date!!)
            binding.layoutWeather.weatherDate.text = dateanddayname
            // setting the icon
            for (i in it.weather) {
                if (i.icon == "01d") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.oned)
                        .into(binding.layoutWeather.weatherImage)

                }

                if (i.icon == "01n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.onen)
                        .into(binding.layoutWeather.weatherImage)

                }

                if (i.icon == "02d") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.twod)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "02n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.twon)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "03d" || i.icon == "03n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.threedn)
                        .into(binding.layoutWeather.weatherImage)

                }



                if (i.icon == "10d") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.tend)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "10n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.tenn)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "04d" || i.icon == "04n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.fourdn)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "09d" || i.icon == "09n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.ninedn)
                        .into(binding.layoutWeather.weatherImage)

                }



                if (i.icon == "11d" || i.icon == "11n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.elevend)
                        .into(binding.layoutWeather.weatherImage)

                }


                if (i.icon == "13d" || i.icon == "13n") {
                    Glide.with(binding.layoutWeather.weatherImage.context)
                        .load(R.drawable.thirteend).into(binding.layoutWeather.weatherImage)

                }

                if (i.icon == "50d" || i.icon == "50n") {
                    Glide.with(binding.layoutWeather.weatherImage.context).load(R.drawable.fiftydn)
                        .into(binding.layoutWeather.weatherImage)

                }

            }
        })

        viewModel.todayWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            adapter.setList(setNewlist)
            binding.recyclerView.adapter = adapter
        })

        viewModel.showToast.observe(this, Observer {
            if (it) {
                Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
            }
        })

        getLastKnownLocation()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Use latitude and longitude
                    viewModel.getWeather(lat = latitude, lon = longitude)
                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

}