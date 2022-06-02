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

package online.hudacek.fxradio.apiclient.stations.model

/**
 * Station data class
 */
data class Station(
    val stationuuid: String,
    val name: String,
    val url_resolved: String?,
    val homepage: String,
    var favicon: String?,
    val tags: String = "",
    val country: String = "",
    val countrycode: String = "",
    val state: String = "",
    val language: String = "",
    val codec: String = "",
    val bitrate: Int = 0,
    var votes: Int = 0,
    val geo_lat: Double = 0.0,
    val geo_long: Double = 0.0,
    val clicktrend: Int = 0,
    val languagecodes: String = "",
) {

    fun isValid() = stationuuid != "0"

    override fun equals(other: Any?) = if (other is Station) {
        this.stationuuid == other.stationuuid
    } else {
        super.equals(other)
    }

    override fun hashCode() = stationuuid.hashCode()

    companion object {
        val dummy by lazy {
            Station("0", "Not playing", null, "http://hudacek.online", null)
        }
    }
}

//Contains tag or country name of station
val Station.tagsSplit: String
    get() {
        val stationTagsSplit = tags.split(",")
        return when {
            tags.isEmpty() -> country
            stationTagsSplit.size > 1 -> stationTagsSplit[0].capitalize() + ", " + stationTagsSplit[1].capitalize()
            else -> stationTagsSplit[0].capitalize()
        }
    }

