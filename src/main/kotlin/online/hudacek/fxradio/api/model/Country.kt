package online.hudacek.fxradio.api.model

import java.util.*

data class Country(val name: String,
                   val stationcount: Int)

data class CountriesBody(val hidebroken: Boolean = true)

val Country.isValid: Boolean
    get() = Locale.getISOCountries().any { Locale("", it).displayCountry == name }

val Country.countryCode: String?
    get() = Locale.getISOCountries().find { Locale("", it).displayCountry == name }