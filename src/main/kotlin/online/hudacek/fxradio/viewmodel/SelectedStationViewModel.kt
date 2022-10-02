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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.usecase.StationClickUseCase
import tornadofx.observableListOf
import tornadofx.property

sealed class InfoPanelState {
    object Hidden : InfoPanelState()

    object Shown : InfoPanelState()
}

class SelectedStation(station: Station) {
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
    var countryState: String? by property(station.state)
    var tags: ObservableList<String> by property(observableListOf(
            station.tags
                    .split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
    ))
    var homePage: String by property(station.homepage)
}

class SelectedStationViewModel : BaseStateViewModel<SelectedStation, InfoPanelState>(
        SelectedStation(Station.dummy), InfoPanelState.Hidden) {
    val stationProperty = bind(SelectedStation::station) as ObjectProperty
    val tagsProperty = bind(SelectedStation::tags) as ListProperty<String>
    val homePageProperty = bind(SelectedStation::homePage) as StringProperty
    val nameProperty = bind(SelectedStation::name) as StringProperty
    val codecProperty = bind(SelectedStation::codec) as StringProperty
    val bitrateProperty = bind(SelectedStation::bitrate) as IntegerProperty
    val languageProperty = bind(SelectedStation::language) as StringProperty
    val countryProperty = bind(SelectedStation::country) as StringProperty
    val votesProperty = bind(SelectedStation::votes) as IntegerProperty
    val streamUrlProperty = bind(SelectedStation::streamUrl) as StringProperty
    val faviconProperty = bind(SelectedStation::favicon) as StringProperty?
    val countryStateProperty = bind(SelectedStation::countryState) as StringProperty?
    val clickTrendProperty = bind(SelectedStation::clickTrend) as IntegerProperty

    private val stationClickUseCase: StationClickUseCase by inject()

    val stationObservable: Observable<Station> = stationProperty
            .toObservableChangesNonNull()
            .map { it.newVal }
            .filter { it.isValid() }
            .doOnEach(appEvent.addToHistory) //Send the new history item event

    init {
        stationObservable.subscribe(stationClickUseCase::execute)
    }
}
/*

 */