package Fragments.responseAI

import Fragments.home.HomeFragment
import Storage.SharedPreferencesManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.card.MaterialCardView
import com.wardrobeweatherAI.R
import com.wardrobeweatherAI.databinding.FragmentResponseaiBinding
import kotlinx.coroutines.launch
import network.api.ChatGPT_API
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.IOException
import kotlin.random.Random

class ResponseAIFragment: Fragment() {
    private var _binding: FragmentResponseaiBinding?=null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentResponseaiBinding.inflate(inflater,container,false)
        //getGPTResponse("")
        //lifecycleScope.launch {getResponse("")}
        getRandomResponse()


        return binding.root
    }
    private val client= OkHttpClient()

    // TENTATIVO DI RISPOSTA CON CHATGPT MA NON VA PERCHé HO TERMINATO I TOKENS
    fun getGPTResponse(query: String){
        val requestBody="""
            {
            "model": "davinci-002",
            "prompt": "$query",
            "max_tokens": 500,
            "temperature": 0        
            }
        """.trimIndent()


        val request= Request.Builder()
            .url(ChatGPT_API.BASE_URL)
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization", "Bearer ${ChatGPT_API.API_KEY}" )
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(requireContext(),"API Failed",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                val jsonObject= JSONObject(body)
                if (body != null) {
                    Log.e("API",body)
                }
                val jsonArray: JSONArray= jsonObject.getJSONArray("choices")
                val text= jsonArray.getJSONObject(0).getString("text")
                binding.textRispostaAI.text=text.toString()
            }
        })
    }

    // TENTATIVO DI RISPOSTA CON GEMINI MA NON VA PERCHE API NON PRESENTE IN ITALIA
     suspend fun getResponse(query: String){
        val generativeModel= GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyBpaVkZjapkTAEq7yw29qM_TQAv_NU4z_w"
        )
        val prompt = "Write a story about a magic backpack."
        val response = generativeModel.generateContent(prompt)
        binding.textRispostaAI.text=response.text.toString()
    }

    fun getRandomResponse(){
        binding.textRispostaAI.text="Con le condizioni meteorologiche presenti, potresti considerare un abbigliamento che ti protegga dal freddo e dalla pioggia. Ecco alcune suggerimenti:\n \n Indossa un maglione o una giacca leggera per tenerti al caldo. Puoi aggiungere o rimuovere strati a seconda delle tue esigenze, dato che la temperatura è moderata.\n" +
                " \n" +
                " Vista l'alta probabilità di pioggia, è consigliabile indossare un impermeabile o una giacca antipioggia per proteggerti dall'acqua.\n" +
                " \n" +
                " Accessori:\n Porta con te un ombrello compatto che possa essere facilmente riposto in borsa nel caso in cui inizi a piovere.\n" +
                " \n" +
                " Copricapo:\n Un cappello o una cuffia possono aiutare a mantenere la testa al caldo, specialmente se è prevista pioggia.\n" +
                " \n" +
                " Ricorda che queste sono solo linee guida generali e puoi personalizzare l'abbigliamento in base alle tue preferenze. Verifica sempre le previsioni del tempo poco prima di uscire, poiché le condizioni possono cambiare rapidamente."
    }

    override fun onPause() {
        super.onPause()
        val card = activity?.findViewById<MaterialCardView>(R.id.cardAI)
        card?.visibility=View.VISIBLE
    }


}