package dipendenze_injection

import Storage.SharedPreferencesManager
import android.content.Context
import com.google.gson.Gson
import org.koin.dsl.module

val storageModule = module {
    single { SharedPreferencesManager(context= get(), gson = get()) }
}