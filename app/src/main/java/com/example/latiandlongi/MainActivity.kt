package com.example.latiandlongi

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.latiandlongi.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserve()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            button.setOnClickListener {
                toggleLocationUpdates()
            }

            updateButton.setOnClickListener {
                viewModel.isUpdateLocationButtonClicked = true
                viewModel.updateLocation(viewModel.lastLocation)
            }
        }

        viewModel.locationRequest = LocationRequest.Builder(Long.MAX_VALUE)
            .setIntervalMillis(5000L) // Update interval of 5 seconds
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        viewModel.locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLocation = locationResult.lastLocation
                viewModel.lastLocation = currentLocation
                if (viewModel.isUpdateLocationButtonClicked) {
                    viewModel.updateLocation(viewModel.lastLocation)
                }
            }
        }
    }

    private fun toggleLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }

        if (!viewModel.locationUpdatesEnabled) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val textLatitude = "Latitude: ${location.latitude}"
                    val textLongitude = "Longitude: ${location.longitude}"
                    binding.latitude.text = textLatitude
                    binding.longitude.text = textLongitude

                    viewModel.lastLocation = location
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                viewModel.locationRequest,
                viewModel.locationCallback,
                null
            )
            viewModel.locationUpdatesEnabled = true
//            binding.updateButton.text = "Update Location"
        }
    }

    private fun initObserve() {
        viewModel.updateLocation.observe(this) { location ->
            //        val textUpdatedLocation = "Updated Location: ${it.latitude}, ${it.longitude}"
            binding.updatedLocation.text = "${location?.latitude} ${location?.longitude}"
        }
    }
}

