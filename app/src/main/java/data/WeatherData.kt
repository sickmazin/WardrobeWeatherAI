package data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class WeatherData

data class CurrentLocation(
    val date: String= getCurrentDate(),
    val location: String= "Dammi la tua posizione",
    val latitude: Double?= null,
    val longitude: Double?= null
):WeatherData()

data class CurrentWeather(
    val icon: String,
    val temperature: Float,
    val vento: Float,
    val umidita: Int,
    val probabilitaPioggia: Int
):WeatherData()

data class Previsioni(
    val time: String,
    val temperatura: Float,
    val tempPercepita: Float,
    val icon: String
): WeatherData()

private fun getCurrentDate(): String {
    val currentDate=Date()
    val formatter=SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return "Today, ${formatter.format(currentDate)}"
}