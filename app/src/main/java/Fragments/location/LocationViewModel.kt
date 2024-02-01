package Fragments.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.RemoteLocation
import kotlinx.coroutines.launch
import network.repository.WeatherDataRepository
import retrofit2.http.Query

class LocationViewModel(private val weatherDataRepository: WeatherDataRepository): ViewModel() {

    private val _searchresult=MutableLiveData<SearchResultDataState>()
    val searchResult: LiveData<SearchResultDataState>get() = _searchresult

      fun searchLocation(query: String){
          viewModelScope.launch {
              emitSearchResultUIState(isLoading = true)
              val searchResult=weatherDataRepository.searchLocation( query)
              if(searchResult.isNullOrEmpty()){
                  emitSearchResultUIState(error="Posizione non trovata, perfavore riprovare")
              }else{
                  emitSearchResultUIState(locations = searchResult)
              }
          }
    }

    private fun emitSearchResultUIState(
        isLoading: Boolean=false,
        locations: List<RemoteLocation>?=null,
        error: String?=null
    ){
        val searchResultDataState= SearchResultDataState(isLoading, locations, error)
        _searchresult.value= searchResultDataState
    }

    data class  SearchResultDataState(
        val isLoading: Boolean,
        val locations: List<RemoteLocation>?,
        val error: String?
    )
}