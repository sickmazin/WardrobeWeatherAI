package dipendenze_injection

import network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule= module {
    single { WeatherDataRepository(weatherAPI =get() ) }
}