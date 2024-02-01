package Utils

import android.app.Application
import dipendenze_injection.networkModule
import dipendenze_injection.repositoryModule
import dipendenze_injection.serializerModule
import dipendenze_injection.storageModule
import dipendenze_injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppConfig: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@AppConfig)
            modules(listOf(repositoryModule, viewModelModule, networkModule, serializerModule,
                storageModule))
        }
    }
}