package Fragments.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wardrobeweatherAI.databinding.ItemContainerlocationBinding
import data.RemoteLocation

class LocationAdapter (private val onLocationClicked : (RemoteLocation) -> Unit): RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){

    private val locations= mutableListOf<RemoteLocation>()

    @SuppressLint("NotifyDataSetChanged")
    fun setdata(data: List<RemoteLocation>){
        locations.clear()
        locations.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            ItemContainerlocationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(remoteLocation = locations[position])
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationViewHolder(
        private val binding: ItemContainerlocationBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(remoteLocation: RemoteLocation){
            with(remoteLocation){
                val location="$name, $region, $country"
                binding.textManualLocation.text=location
                binding.root.setOnClickListener { onLocationClicked(remoteLocation) }
            }
        }
    }
}