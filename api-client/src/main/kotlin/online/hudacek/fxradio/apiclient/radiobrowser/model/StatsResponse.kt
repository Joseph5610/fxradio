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

data class StatsResponse(
    @JsonProperty("supported_version") val supportedVersion: String = "",
    @JsonProperty("software_version") val softwareVersion: String = "",
    val status: String = "",
    val stations: String = "",
    @JsonProperty("stations_broken") val stationsBroken: String = "",
    val tags: String = "",
    @JsonProperty("clicks_last_hour") val clicksLastHour: Int = 0,
    @JsonProperty("clicks_last_day") val clicksLastDay: Int = 0,
    val languages: Int = 0,
    val countries: Int = 0
)
