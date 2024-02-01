package com.wardrobeweatherAI

import Fragments.responseAI.ResponseAIFragment
import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button= findViewById<Button>(R.id.buttonForResponse)
        val card = findViewById<MaterialCardView>(R.id.cardAI)

        button.setOnClickListener {
            Navigation.findNavController(this,R.id.weatherDataRecyclerView).navigate(R.id.action_home_to_risposta_fragment);
            card.visibility= View.GONE
        }
    }
}