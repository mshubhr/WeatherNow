package com.project.weathernow.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.project.weathernow.R
import com.project.weathernow.databinding.ActivityMainBinding
import com.project.weathernow.db.WeatherDatabase
import com.project.weathernow.viewModel.WeatherRepository
import com.project.weathernow.viewModel.WeatherViewModel
import com.project.weathernow.viewModel.WeatherViewModelProviderFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: WeatherToday

    private val outputDateFormat = SimpleDateFormat("d MMMM EEEE, HH:mm", Locale.getDefault())
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private val weatherRepository by lazy {
        WeatherRepository(WeatherDatabase(applicationContext))
    }
    private val viewModelFactory by lazy {
        WeatherViewModelProviderFactory(application, weatherRepository)
    }
    private val viewModel: WeatherViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WeatherToday()
        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@MainActivity.adapter
        }

        binding.searchCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.getWeather(binding.searchCity.text.toString())
                true
            } else {
                false
            }
        }

        viewModel.cityName.observe(this) { name ->
            binding.layoutWeather.weatherCity.text = name
        }

        viewModel.weatherLiveData.observe(this) { weather ->
            weather ?: return@observe
            binding.parentView.visibility = View.VISIBLE

            binding.layoutWeather.apply {
                weatherType.text = weather.weather.firstOrNull()?.description
                weatherTemp.text =
                    getString(R.string.temp_format, weather.main?.temp?.minus(273.15) ?: 0.0)
                weatherHumidity.text =
                    getString(R.string.humidity_format, weather.main?.humidity ?: 0)
                weatherWind.text = getString(R.string.speed_format, weather.wind?.speed ?: 0.0)
                weatherRain.text = getString(R.string.rain_format, weather.clouds?.all ?: 0)

                weatherDate.text = weather.dtTxt?.let { dt ->
                    try {
                        inputDateFormat.parse(dt)?.let { outputDateFormat.format(it) }
                    } catch (_: Exception) {
                        dt
                    }
                } ?: ""

                Glide.with(this@MainActivity)
                    .load(WeatherUtils.getWeatherIcon(weather.weather.firstOrNull()?.icon))
                    .into(weatherImage)
            }
        }

        viewModel.todayWeatherLiveData.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.showToast.observe(this) { show ->
            if (show) Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
        }

        getLastKnownLocation()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.getWeather(lat = it.latitude, lon = it.longitude)
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
}