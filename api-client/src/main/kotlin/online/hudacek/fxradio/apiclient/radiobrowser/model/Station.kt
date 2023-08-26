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

import com.google.gson.annotations.SerializedName
import online.hudacek.fxradio.apiclient.ApiUtils.COUNTRY_IGNORE_LIST
import java.io.Serializable

private const val DUMMY_STATION_URL = "https://hudacek.online"
private const val DUMMY_STATION_NAME = "Nothing playing"
private const val INVALID_UUID = "0"

/**
 * Station data class
 */
data class Station(
    @SerializedName("stationuuid") val uuid: String,
    val name: String,
    @SerializedName("url_resolved") val urlResolved: String,
    val homepage: String,
    val favicon: String?,
    val tags: String = "",
    val country: String = "",
    @SerializedName("countrycode") val countryCode: String = "",
    val state: String = "",
    val language: String = "",
    val codec: String = "",
    val bitrate: Int = 0,
    val votes: Int = 0,
    @SerializedName("geo_lat") val geoLat: Double = 0.0,
    @SerializedName("geo_long") val geoLong: Double = 0.0,
    @SerializedName("clicktrend") val clickTrend: Int = 0,
    @SerializedName("clickcount") val clickCount: Int = 0,
    @SerializedName("languagecodes") val languageCodes: String = "",
    @SerializedName("has_extended_info") val hasExtendedInfo: Boolean = false
) : Serializable {

    fun isValid() = uuid != INVALID_UUID

    override fun equals(other: Any?) = if (other is Station) {
        this.uuid == other.uuid
    } else {
        super.equals(other)
    }

    override fun hashCode() = uuid.hashCode()

    companion object {

        val dummy by lazy {
            Station(INVALID_UUID, DUMMY_STATION_NAME, DUMMY_STATION_URL, DUMMY_STATION_URL, null)
        }
    }
}

/**
 * Contains first 2 tags or country name of station
 */
val Station.description: String
    get() {
        val stationTagsSplit = tags.split(",")
        return when {
            tags.isEmpty() -> country
            stationTagsSplit.size > 1 -> stationTagsSplit[0].capitals() + ", " + stationTagsSplit[1].capitals()
            else -> stationTagsSplit[0].capitals()
        }
    }

val Station.isIgnoredStation: Boolean
    get() = COUNTRY_IGNORE_LIST.contains(countryCode)

private fun String.capitals() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
