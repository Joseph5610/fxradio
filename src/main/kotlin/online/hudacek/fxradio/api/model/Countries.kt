package online.hudacek.fxradio.api.model

import java.util.*

//Response
data class Countries(val name: String, val stationcount: Int)

//GET params
data class CountriesBody(val hidebroken: Boolean = true)

val Countries.isValidCountry: Boolean
    get() = Locale.getISOCountries().any { Locale("", it).displayCountry == name }

val Countries.countryCode: String?
    get() = Locale.getISOCountries().find { Locale("", it).displayCountry == name }
