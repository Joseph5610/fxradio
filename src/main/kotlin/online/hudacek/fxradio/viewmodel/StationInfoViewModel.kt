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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.stations.model.Station
import tornadofx.observableListOf
import tornadofx.property

sealed class InfoPanelState {
    object Hidden : InfoPanelState()

    object Shown : InfoPanelState()
}

class StationInfo(station: Station) {
    var station: Station by property(station)
    var name: String by property(station.name)
    var country: String by property(station.country)
    var language: String by property(station.language)
    var codec: String by property(station.codec)
    var bitrate: Int by property(station.bitrate)
    var votes: Int by property(station.votes)
    var streamUrl: String by property(station.url_resolved)
    var clickTrend: Int by property(station.clicktrend)
    var favicon: String? by property(station.favicon)
    var tags: ObservableList<String> by property(observableListOf(
            station.tags
                    .split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
    ))
    var homePage: String by property(station.homepage)
}

class StationInfoViewModel : BaseStateViewModel<StationInfo, InfoPanelState>(
        StationInfo(Station.dummy), InfoPanelState.Hidden) {
    val stationProperty = bind(StationInfo::station) as ObjectProperty
    val tagsProperty = bind(StationInfo::tags) as ListProperty<String>
    val homePageProperty = bind(StationInfo::homePage) as StringProperty
    val nameProperty = bind(StationInfo::name) as StringProperty
    val codecProperty = bind(StationInfo::codec) as StringProperty
    val bitrateProperty = bind(StationInfo::bitrate) as IntegerProperty
    val languageProperty = bind(StationInfo::language) as StringProperty
    val countryProperty = bind(StationInfo::country) as StringProperty
    val votesProperty = bind(StationInfo::votes) as IntegerProperty
    val streamUrlProperty = bind(StationInfo::streamUrl) as StringProperty
    val faviconProperty = bind(StationInfo::favicon) as StringProperty?
    val clickTrendProperty = bind(StationInfo::clickTrend) as IntegerProperty
}
