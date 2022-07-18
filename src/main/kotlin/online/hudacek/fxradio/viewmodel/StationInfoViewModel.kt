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

import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.stations.model.Station
import tornadofx.observableListOf
import tornadofx.property

class StationInfo(station: Station, showPanel: Boolean = false) {
    var station: Station by property(station)
    var name: String by property(station.name)
    var country: String by property(station.country)
    var language: String by property(station.language)
    var codec: String by property(station.codec)
    var bitrate: Int by property(station.bitrate)
    var votes: Int by property(station.votes)
    var tags: ObservableList<String> by property(observableListOf(
            station.tags
                    .split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
    ))
    var homePage: String by property(station.homepage)
    var showPanel: Boolean by property(showPanel)
}

class StationInfoViewModel : BaseViewModel<StationInfo>(StationInfo(Station.dummy)) {
    val stationProperty = bind(StationInfo::station) as ObjectProperty
    val tagsProperty = bind(StationInfo::tags) as ListProperty<String>
    val homePageProperty = bind(StationInfo::homePage) as StringProperty
    val nameProperty = bind(StationInfo::name) as StringProperty
    val codecProperty = bind(StationInfo::codec) as StringProperty
    val bitrateProperty = bind(StationInfo::bitrate) as IntegerProperty
    val languageProperty = bind(StationInfo::language) as StringProperty
    val countryProperty = bind(StationInfo::country) as StringProperty
    val votesProperty = bind(StationInfo::votes) as IntegerProperty
    val showPanelProperty = bind(StationInfo::showPanel) as BooleanProperty
}