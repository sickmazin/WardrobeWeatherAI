package dipendenze_injection

import network.api.ChatGPT_API
import network.api.WeatherAPI
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory { okHttpClient() }
    single { retrofit(okHttpClient= get ()) }
    factory { weatherAPI(retrofit= get()) }
}


private fun okHttpClient()= OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .retryOnConnectionFailure(false)
    .build()

private fun retrofit(okHttpClient: OkHttpClient)= Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(WeatherAPI.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
private fun weatherAPI(retrofit: Retrofit)= retrofit.create(WeatherAPI::class.java)