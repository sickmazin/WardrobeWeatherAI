package Fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wardrobeweatherAI.R
import com.wardrobeweatherAI.databinding.ItemContainerButtonForAiBinding
import com.wardrobeweatherAI.databinding.ItemContainerPrevisioniBinding
import com.wardrobeweatherAI.databinding.ItemContainerWeatherBinding
import com.wardrobeweatherAI.databinding.ItemCurrentlocationBinding
import data.CurrentLocation
import data.CurrentWeather
import data.Previsioni
import data.WeatherData


class WeatherDataAdapter (
    private val onLocationClicked:()-> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val INDEX_CURRENT_LOCATION=0
        const val INDEX_CURRENT_WEATHER=1
        const val INDEX_PREVISIONI=2
    }

    private val weatherData= mutableListOf<WeatherData>()

    fun setPrevisioniData(previsioni: List<Previsioni>){
        weatherData.removeAll(){it is Previsioni}
        notifyItemRangeRemoved(INDEX_PREVISIONI,weatherData.size)
        weatherData.addAll(INDEX_PREVISIONI,previsioni)
        notifyItemRangeChanged(INDEX_PREVISIONI,weatherData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType){
            INDEX_CURRENT_LOCATION -> CurrentLocationViewHolder(
                ItemCurrentlocationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,false))
            INDEX_PREVISIONI -> PrevisioneViewHolder(
                ItemContainerPrevisioniBinding.inflate(
                    LayoutInflater.from(parent.context), parent,false
                )
            )
            else -> CurrentWeatherViewHolder(
                ItemContainerWeatherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(weatherData[position]){
            is CurrentLocation -> INDEX_CURRENT_LOCATION
            is CurrentWeather -> INDEX_CURRENT_WEATHER
            is Previsioni -> INDEX_PREVISIONI
        }
    }

    fun setCurrentLocation(currentLocation: CurrentLocation){
        if (weatherData.isEmpty()) {
            weatherData.add(INDEX_CURRENT_LOCATION, currentLocation)
            notifyItemInserted(INDEX_CURRENT_LOCATION)
        }else{
            weatherData[INDEX_CURRENT_LOCATION]=currentLocation
            notifyItemChanged(INDEX_CURRENT_LOCATION)
        }
    }

    fun setCurrentWeather(currentWeather: CurrentWeather){
        if(weatherData.getOrNull(INDEX_CURRENT_WEATHER)!=null){
            weatherData[INDEX_CURRENT_WEATHER]=currentWeather
            notifyItemChanged(INDEX_CURRENT_WEATHER)
        }else{
            weatherData.add(INDEX_CURRENT_WEATHER,currentWeather)
            notifyItemInserted(INDEX_CURRENT_WEATHER)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CurrentLocationViewHolder -> holder.bind(weatherData[position]as CurrentLocation)
            is CurrentWeatherViewHolder -> holder.bind(weatherData[position]as CurrentWeather)
            is PrevisioneViewHolder -> holder.bind(weatherData[position] as Previsioni)
        }
    }

    inner class CurrentLocationViewHolder(
        private val binding: ItemCurrentlocationBinding
    ):RecyclerView.ViewHolder(binding.root) {
        fun bind(currentLocation: CurrentLocation) {
            with(binding) {
                textDataOdierna.text= currentLocation.date
                textCurrentLocation.text=currentLocation.location
                imageShareLocation.setOnClickListener {onLocationClicked()}
                textCurrentLocation.setOnClickListener {onLocationClicked()}
            }
        }
    }

    inner class CurrentWeatherViewHolder(
        private val binding : ItemContainerWeatherBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(currentWeather: CurrentWeather){
            with(binding){
                imageIcon.load("https:${currentWeather.icon}") {crossfade(true)}
                textTemperature.text=String.format("%s\u00B0C",currentWeather.temperature)
                textUmidit.text=String.format("%s%%",currentWeather.umidita)
                textVento.text=String.format("%s km/h",currentWeather.vento)
                textProbPioggia.text=String.format("%s%%",currentWeather.probabilitaPioggia)
            }
        }
    }
    inner class PrevisioneViewHolder(private val binding: ItemContainerPrevisioniBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(previsioni: Previsioni){
            with(binding){
                imageIcon.load("https:${previsioni.icon}"){crossfade(true)}
                textTemperatura.text=String.format("Temp: %s\u00B0C",previsioni.temperatura)
                textTemperaturaPercepita.text=String.format("Percepita: %s\u00B0C",previsioni.tempPercepita)
                textTime.text=previsioni.time
            }
        }
    }
}