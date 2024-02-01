package data

import com.google.gson.annotations.SerializedName

data class RemoteWeatherData(
    val current: CurrentWeatherRemote,
    val forecast: ForecastRemote
)
data class ForecastRemote(
    @SerializedName("forecastday")val forecastDay: List<ForecastDayRemote>,

)
data class CurrentWeatherRemote(
    @SerializedName("temp_c") val temperatura: Float,
    val condition: WeatherConditionRemote,
    @SerializedName("wind_kph") val vento: Float,
    val humidity: Int
)
data class ForecastDayRemote(
    val day: DayRemote,
    val hour: List<ForecastHourRemote>
)
data class DayRemote(
    @SerializedName("daily_chance_of_rain") val probabilitaPioggia: Int
)
data class ForecastHourRemote(
    val time: String,
    @SerializedName("temp_c") val temperatura: Float,
    @SerializedName("feelslike_c") val temperaturaPercepita: Float,
    val condition: WeatherConditionRemote

)
data class WeatherConditionRemote(
    val icon:String
)
