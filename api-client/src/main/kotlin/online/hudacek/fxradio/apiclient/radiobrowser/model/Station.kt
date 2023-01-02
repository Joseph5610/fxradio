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
import java.io.Serializable

private const val dummyStationUrl = "https://hudacek.online"

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

    fun isValid() = uuid != "0"

    override fun equals(other: Any?) = if (other is Station) {
        this.uuid == other.uuid
    } else {
        super.equals(other)
    }

    override fun hashCode() = uuid.hashCode()

    companion object {
        val dummy by lazy {
            Station("0", "Nothing playing", dummyStationUrl, dummyStationUrl, null)
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

private fun String.capitals() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
