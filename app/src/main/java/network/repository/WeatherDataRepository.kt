package network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import data.CurrentLocation
import data.RemoteLocation
import data.RemoteWeatherData
import network.api.WeatherAPI

class WeatherDataRepository(private val weatherAPI: WeatherAPI) {
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure : () -> Unit
    ){
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener{location->
            location?: onFailure()
            if (location != null) {
                onSuccess(
                    CurrentLocation(
                        latitude = location.latitude,
                        longitude= location.longitude
                    )
                )
            }
        }.addOnFailureListener { onFailure() }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ):CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation
        return geocoder.getFromLocation(latitude,longitude,1)?.let { indirizzi ->
            val indirizzo= indirizzi[0]
            val indirizzoText= StringBuilder()
            indirizzoText.append(indirizzo.locality).append(",")
            indirizzoText.append(indirizzo.adminArea).append(",")
            indirizzoText.append(indirizzo.countryName)
            currentLocation.copy(
                location = indirizzoText.toString()
            )
        }?:currentLocation
    }

    suspend fun searchLocation (query: String): List<RemoteLocation>? {
        val response= weatherAPI.searchLocation(query = query)
        return  if(response.isSuccessful)response.body()else null
    }

    suspend fun getWeatherData(latitude: Double,longitude: Double):RemoteWeatherData?{
        val response= weatherAPI.getWeatherData(query = "$latitude,$longitude")
        return  if(response.isSuccessful)response.body()else null
    }
}