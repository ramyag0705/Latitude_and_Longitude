package com.example.latiandlongi

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult


class MainViewModel (
    private val application: Application
        ): ViewModel() {

    class Factory(
        private val application: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            MainViewModel(application) as T
    }

    private val _updateLocation = MutableLiveData<Location?>()
    val updateLocation: LiveData<Location?> = _updateLocation

      lateinit var locationRequest: LocationRequest
      lateinit var locationCallback: LocationCallback
     var locationUpdatesEnabled = false
     var lastLocation: Location? = null
     var isUpdateLocationButtonClicked = false


    fun updateLocation(location: Location?) {
        location?.let {
            _updateLocation.value = location
        }
    }
}