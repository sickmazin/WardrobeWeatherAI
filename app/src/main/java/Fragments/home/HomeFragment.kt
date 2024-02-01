package Fragments.home

import Storage.SharedPreferencesManager
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.wardrobeweatherAI.R
import data.CurrentLocation
import com.wardrobeweatherAI.databinding.FragmentHomeBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment: Fragment() {

    companion object{
        const val REQUEST_KEY_MANUAL_LOCATION_SEARCH ="manualLocationSearch"
        const val KEY_LOCATION="locationText"
        const val KEY_LATITUDE="latitude"
        const val KEY_LONGITUDE="longitude"
    }

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = requireNotNull(_binding)

    private val homeViewModel:HomeViewModel by viewModel()
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val geocoder by  lazy { Geocoder(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentHomeBinding.inflate(inflater,container,false)

        return binding.root
    }

    private val weatherDataAdapter= WeatherDataAdapter(
        onLocationClicked={
            showLocationOptions()
        }
    )

    private val locationPermissionLauncher= registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if(isGranted)
            getCurrentLocation()
        else
            Toast.makeText(requireContext(),"Permesso negato",Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentLocation() {
        Toast.makeText(requireContext(),"Permesso concesso",Toast.LENGTH_SHORT).show()
        homeViewModel.getCurrentLocation(fusedLocationProviderClient, geocoder)

    }

    private fun requestLocationPermission(){
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private fun isLocationPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
    }
    private fun proceedWithCurrentLocation(){
        if(isLocationPermissionGranted())
            getCurrentLocation()
        else
            requestLocationPermission()
    }

    private fun showLocationOptions(){
        val option= arrayOf("Posizione attuale","Ricerca manuale")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Scegli metodo di ricerca posizione")
            setItems(option){ _,wich ->
                when(wich) {
                    0 -> proceedWithCurrentLocation()
                    1 -> startManualSearchLocation()
                }
            }
            show()
        }
    }

    private fun setCurrentLocation(currentLocation: CurrentLocation? = null){
        weatherDataAdapter.setCurrentLocation(currentLocation?: CurrentLocation())
        currentLocation?.let { getWeatherData(currentLocation=it) }
    }

    private  fun setObservers(){
        with(homeViewModel){
            currentLocation.observe(viewLifecycleOwner){
                val currentLocationDataState=it.getContentIfNotHandled()?:return@observe
                if(currentLocationDataState.isLoading)
                    showLoading()
                currentLocationDataState.currentLocation?.let { currentLocation ->
                    hideLoading()
                    sharedPreferencesManager.saveCurrentLocation(currentLocation)
                    setCurrentLocation(currentLocation)
                }
                currentLocationDataState.error?.let { error->
                    hideLoading()
                    Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                }
            }
            weatherData.observe(viewLifecycleOwner){
                val weatherDataState = it.getContentIfNotHandled() ?: return@observe
                binding.swipeRefreshLayout.isRefreshing= weatherDataState.isLoading
                weatherDataState.currentWeather?.let {currentWeather ->
                    weatherDataAdapter.setCurrentWeather(currentWeather)
                }
                weatherDataState.previsioni?.let{ previsioni ->
                    weatherDataAdapter.setPrevisioniData(previsioni)

                }
                weatherDataState.error?.let { error ->
                    Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setCurrentLocation(currentLocation = sharedPreferencesManager.getCurrentLocation())
        setListeners()
        setObservers()
    }

    private fun setWeatherDataAdapter() {
        binding.weatherDataRecyclerView.itemAnimator=null
        binding.weatherDataRecyclerView.adapter=weatherDataAdapter
    }

    private fun setListeners(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            hideLoading()
        }
    }

    private fun showLoading(){
        with(binding){
            weatherDataRecyclerView.visibility=View.VISIBLE
            swipeRefreshLayout.isEnabled=false
            swipeRefreshLayout.isRefreshing=true
        }
    }
    private fun hideLoading(){
        with(binding){
            weatherDataRecyclerView.visibility=View.VISIBLE
            swipeRefreshLayout.isEnabled=true
            swipeRefreshLayout.isRefreshing=false
        }
    }
    private val sharedPreferencesManager: SharedPreferencesManager by inject()


    private fun startManualSearchLocation(){
        hideButtonForAI()
        startListeningManualLocationSelection()
        findNavController().navigate(R.id.action_home_to_location_fragment)
    }

    fun startListeningManualLocationSelection(){
        setFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH){_,scelta ->
            stopListeningManualLocationSelection()
            val currentLocation=CurrentLocation(
                location = scelta.getString(KEY_LOCATION)?:"N/A",
                latitude = scelta.getDouble(KEY_LATITUDE),
                longitude = scelta.getDouble(KEY_LONGITUDE)
            )
            showButtonForAI()
            sharedPreferencesManager.saveCurrentLocation(currentLocation)
            setCurrentLocation(currentLocation)
        }
    }

    private fun showButtonForAI() {
        val card = activity?.findViewById<MaterialCardView>(R.id.cardAI)
        card?.visibility=View.VISIBLE
    }
    private fun hideButtonForAI(){
        val card = activity?.findViewById<MaterialCardView>(R.id.cardAI)
        card?.visibility=View.GONE
    }

    private fun stopListeningManualLocationSelection(){
        clearFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH)
    }

    private fun getWeatherData(currentLocation: CurrentLocation){
        if (currentLocation.latitude!=null && currentLocation.longitude!=null ){
            homeViewModel.getWeatherData(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )
        }
    }
}