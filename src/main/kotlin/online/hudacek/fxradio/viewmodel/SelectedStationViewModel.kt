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
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.usecase.search.StationSearchUUIDUseCase
import tornadofx.observableListOf
import tornadofx.property

private val logger = KotlinLogging.logger {}

sealed class InfoPanelState {
    object Hidden : InfoPanelState()

    object Shown : InfoPanelState()
}

class SelectedStation(station: Station) {
    var station: Station by property(station)
    var uuid: String by property(station.uuid)
    var name: String by property(station.name)
    var country: String by property(station.country)
    var language: String by property(station.language)
    var codec: String by property(station.codec)
    var bitrate: Int by property(station.bitrate)
    var votes: Int by property(station.votes)
    var streamUrl: String by property(station.urlResolved)
    var clickTrend: Int by property(station.clickTrend)
    var clickCount: Int by property(station.clickCount)
    var favicon: String? by property(station.favicon)
    var countryState: String? by property(station.state)
    var hasExtendedInfo: Boolean by property(station.hasExtendedInfo)
    var tags: ObservableList<String> by property(observableListOf(
        station.tags
            .split(",")
            .map { tag -> tag.trim() }
            .filter { tag -> tag.isNotEmpty() }
    ))
    var homePage: String by property(station.homepage)
}

class SelectedStationViewModel : BaseStateViewModel<SelectedStation, InfoPanelState>(
    SelectedStation(Station.dummy), InfoPanelState.Hidden
) {

    private val stationSearchUUIDUseCase: StationSearchUUIDUseCase by inject()

    val stationProperty = bind(SelectedStation::station) as ObjectProperty
    val tagsProperty = bind(SelectedStation::tags) as ListProperty<String>
    val uuidProperty = bind(SelectedStation::uuid) as StringProperty
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
    val clickCountProperty = bind(SelectedStation::clickCount) as IntegerProperty
    val hasExtendedInfoProperty = bind(SelectedStation::hasExtendedInfo) as BooleanProperty

    val stationObservable: Observable<Station> = stationProperty
        .toObservableChangesNonNull()
        .map { it.newVal }
        .filter { it.isValid() }
        .doOnEach(appEvent.stationsHistory) //Send the new history item

    /**
     * Retrieve additional station data as some of them might not be known at all times
     */
    fun retrieveAdditionalData() {
        stationObservable
            .flatMapSingle { stationSearchUUIDUseCase.execute(it.uuid) }
            .subscribe({
                it.firstOrNull()?.let { s ->
                    votesProperty.value = s.votes
                    clickTrendProperty.value = s.clickTrend
                    clickCountProperty.value = s.clickCount
                }
            }, {
                logger.debug(it) { "Retrieving additional station data unsuccessful." }
            })
    }
}
