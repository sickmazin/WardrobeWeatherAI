package network.api

import data.RemoteLocation
import data.RemoteWeatherData
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    companion object {
        const val BASE_URL= "https://api.weatherapi.com/v1/"
        const val API_KEY= "6055e5e40dd84dd6a5692912241301"
        val JSON= "application/json; charset=utf8".toMediaType()
    }
    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String= API_KEY,
        @Query("q") query: String
    ): Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String= API_KEY,
        @Query("q") query: String
    ): Response<RemoteWeatherData>
}