package network.api

import data.RemoteLocation
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatGPT_API {
    companion object {
        const val BASE_URL= "https://api.openai.com/v1/completions"
        const val API_KEY= "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"
    }

}