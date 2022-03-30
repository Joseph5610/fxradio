/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.apiclient.stations.model

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