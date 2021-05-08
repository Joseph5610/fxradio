package online.hudacek.fxradio.api.model

import java.util.*

data class Country(val name: String,
                   val stationcount: Int) {

    //Don't use stationCount when comparing this data class
    override fun equals(other: Any?) = if (other is Country) {
        this.name == other.name
    } else {
        super.equals(other)
    }

    override fun hashCode() = name.hashCode()
}

data class CountriesBody(val hidebroken: Boolean = true)

val Country.isValid: Boolean
    get() = Locale.getISOCountries().any { Locale("", it).displayCountry == name }

val Country.countryCode: String?
    get() = Locale.getISOCountries().find { Locale("", it).displayCountry == name }