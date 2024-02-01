package Fragments.home

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.card.MaterialCardView
import com.wardrobeweatherAI.R
import data.CurrentLocation
import data.CurrentWeather
import data.LiveDataEvent
import data.Previsioni
import kotlinx.coroutines.launch
import network.repository.WeatherDataRepository
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(private val weatherDataRepository: WeatherDataRepository): ViewModel(){

    //region Posizione Attuale e selezione
    private val _currentLocation = MutableLiveData<LiveDataEvent<CurrentLocationDataState>>()
    val  currentLocation: LiveData<LiveDataEvent<CurrentLocationDataState>> get()= _currentLocation



    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ){
        viewModelScope.launch {
            emitCurrentLocationUIState(isLoading = true)
            weatherDataRepository.getCurrentLocation(
                fusedLocationProviderClient=fusedLocationProviderClient,
                onSuccess = { currentLocation ->
                    updateIndirizzoText(currentLocation, geocoder)
                }, onFailure = {
                    emitCurrentLocationUIState(error = "Impossibile ottenere la posizione!")
                }
            )
        }
    }

    private fun updateIndirizzoText(currentLocation: CurrentLocation,geocoder: Geocoder){
        viewModelScope.launch {
            runCatching {
                weatherDataRepository.updateAddressText(currentLocation,geocoder)
            }.onSuccess { location ->
                emitCurrentLocationUIState(currentLocation=location)
            }.onFailure {
                emitCurrentLocationUIState(currentLocation= currentLocation.copy(location="N/A"))
            }
            val location=weatherDataRepository.updateAddressText(currentLocation, geocoder)
            emitCurrentLocationUIState(currentLocation= location)
        }
    }

    private fun emitCurrentLocationUIState(
        isLoading: Boolean=false,
        currentLocation: CurrentLocation? =null,
        error: String?= null
    ){
        val currentLocationDataState= CurrentLocationDataState(isLoading, currentLocation, error)
        _currentLocation.value=LiveDataEvent(currentLocationDataState)
    }

    data class CurrentLocationDataState(
        val isLoading: Boolean,
        val currentLocation: CurrentLocation?,
        val error: String?
    )
    //endregion

    //region Meteo

    private val _weatherData= MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData: LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData

    fun getWeatherData(latitude: Double, longitude: Double){
        viewModelScope.launch {
            emitWeatherDataUIState(isLoading = true)
            weatherDataRepository.getWeatherData(latitude, longitude)?.let { weatherData->
                emitWeatherDataUIState(
                    currentWeather = CurrentWeather(
                        icon = weatherData.current.condition.icon,
                        temperature = weatherData.current.temperatura,
                        vento = weatherData.current.vento,
                        umidita = weatherData.current.humidity,
                        probabilitaPioggia = weatherData.forecast.forecastDay.first().day.probabilitaPioggia
                    ),
                    previsioni= weatherData.forecast.forecastDay.first().hour.map {
                        Previsioni(
                            time = getPrevisioniTime(it.time),
                            temperatura = it.temperatura,
                            tempPercepita = it.temperaturaPercepita,
                            icon = it.condition.icon
                        )
                    }
                )
            }?:emitWeatherDataUIState(error= "Impossibile leggere i dati delle previsioni")
        }
    }

    private fun emitWeatherDataUIState(
        isLoading: Boolean =false,
        currentWeather: CurrentWeather?= null,
        previsioni: List<Previsioni>?=null,
        error: String?=null
    ){
        val weatherDataState= WeatherDataState(isLoading, currentWeather,previsioni, error)
        _weatherData.value= LiveDataEvent(weatherDataState)
    }


    data class WeatherDataState(
        val isLoading: Boolean,
        val currentWeather: CurrentWeather?,
        val previsioni: List<Previsioni>?,
        val error: String?
    )

    private fun getPrevisioniTime(dateTime:String): String{
        val pattern= SimpleDateFormat("yyy-MM-dd HH:mm", Locale.getDefault())
        val date= pattern.parse(dateTime)?: return dateTime
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }


    //endregion

}