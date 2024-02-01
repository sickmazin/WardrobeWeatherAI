package Fragments.location

import Fragments.home.HomeFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wardrobeweatherAI.R
import com.wardrobeweatherAI.databinding.FragmentLocationBinding
import data.RemoteLocation
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationFragment: Fragment() {
    private var _binding: FragmentLocationBinding?=null
    private val binding get() = requireNotNull(_binding)



    private val locationViewModel: LocationViewModel by viewModel()

    private val locationsAdapter=LocationAdapter(
        onLocationClicked ={remoteLocation ->  setLocation(remoteLocation)}
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setupPosizioniRecyclerView()
        setObservers()
    }
    private fun setListeners(){
        binding.imageClose.setOnClickListener { findNavController().popBackStack() }
        binding.inputSearch.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId!= KeyEvent.KEYCODE_ENTER){
                hideSoftKeyboard()
                val query=binding.inputSearch.editText?.text
                if(query.isNullOrBlank()) return@setOnEditorActionListener true
                searchLocation(query.toString())
            }
            return@setOnEditorActionListener true
        }
    }

    private fun setupPosizioniRecyclerView(){
        with(binding.posizioniRecyclerView){
            addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
            adapter= locationsAdapter
        }
    }

    override fun onPause() {
        super.onPause()
        val card = activity?.findViewById<MaterialCardView>(R.id.cardAI)
        card?.visibility=View.VISIBLE
    }
    private fun setLocation(remoteLocation: RemoteLocation){
        with(remoteLocation){
            val locationtext= "$name, $region, $country"
            setFragmentResult(
                requestKey = HomeFragment.REQUEST_KEY_MANUAL_LOCATION_SEARCH,
                result = bundleOf(
                    HomeFragment.KEY_LOCATION to locationtext,
                    HomeFragment.KEY_LATITUDE to lat,
                    HomeFragment.KEY_LONGITUDE to lon
                )
            )
            findNavController().popBackStack()
        }
    }

    private fun setObservers(){
        locationViewModel.searchResult.observe(viewLifecycleOwner){
            val searchResultDataState= it ?: return@observe
            if(searchResultDataState.isLoading){
                binding.posizioniRecyclerView.visibility= View.GONE
                binding.progressBar.visibility=View.VISIBLE
            }else {
                binding.progressBar.visibility=View.GONE
            }
            searchResultDataState.locations?.let { remoteLocations ->
                Toast.makeText(requireContext(), "${remoteLocations.size} posizioni trovate", Toast.LENGTH_SHORT).show()
                binding.posizioniRecyclerView.visibility=View.VISIBLE
                locationsAdapter.setdata(remoteLocations)

            }
            searchResultDataState.error?.let { error->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(query: String ){
        locationViewModel.searchLocation(query)
    }

    private fun hideSoftKeyboard(){
        val inputManager= requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            binding.inputSearch.editText?.windowToken,0
        )
    }
}