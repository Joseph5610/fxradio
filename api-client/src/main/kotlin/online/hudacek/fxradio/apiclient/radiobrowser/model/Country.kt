/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.apiclient.radiobrowser.model

import com.fasterxml.jackson.annotation.JsonProperty
import online.hudacek.fxradio.apiclient.ApiUtils.COUNTRY_IGNORE_LIST

data class Country(
    val name: String,
    @JsonProperty("iso_3166_1") val iso3166: String,
    @JsonProperty("stationcount") val stationCount: Int
) {

    // Don't use stationCount when comparing this data class
    override fun equals(other: Any?) = if (other is Country) {
        this.iso3166 == other.iso3166
    } else {
        super.equals(other)
    }

    override fun hashCode() = iso3166.hashCode()
}

val Country.isIgnoredCountry: Boolean
    get() = COUNTRY_IGNORE_LIST.contains(iso3166)
