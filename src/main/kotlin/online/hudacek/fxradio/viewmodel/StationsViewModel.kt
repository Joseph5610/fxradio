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

import io.reactivex.rxjava3.core.Single
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.usecase.station.GetPopularStationsUseCase
import online.hudacek.fxradio.usecase.station.GetStationsByCountryUseCase
import online.hudacek.fxradio.usecase.station.GetTrendingStationsUseCase
import online.hudacek.fxradio.usecase.station.StationVoteUseCase
import online.hudacek.fxradio.util.toObservableChanges
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.asObservable
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

sealed class StationsState {
    data class Fetched(val stations: List<Station>) : StationsState()
    data class Error(val cause: String) : StationsState()
    object NoStations : StationsState()
    object ShortQuery : StationsState()
}

class Stations(stations: ObservableList<Station> = observableListOf()) {
    var stations: ObservableList<Station> by property(stations)
}

/**
 * Holds information about currently shown
 * stations inside [online.hudacek.fxradio.ui.view.stations.StationsDataGridView]
 */
class StationsViewModel : BaseStateViewModel<Stations, StationsState>(Stations(), StationsState.NoStations) {

    private val libraryViewModel: LibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    private val getPopularStationsUseCase: GetPopularStationsUseCase by inject()
    private val getTrendingStationsUseCase: GetTrendingStationsUseCase by inject()
    private val getStationsByCountryUseCase: GetStationsByCountryUseCase by inject()
    private val stationVoteUseCase: StationVoteUseCase by inject()

    val stationsProperty = bind(Stations::stations) as ListProperty

    init {
        libraryViewModel.stateObservable.subscribe(::handleNewLibraryState)

        searchViewModel.searchByTagProperty.toObservableChanges().subscribe { search() }

        searchViewModel.queryChanges.subscribe { search() }

        favouritesViewModel.stationsObservable
            .subscribe {
                if (libraryViewModel.stateProperty.value == LibraryState.Favourites) {
                    show(it)
                }
            }

        // Increase vote count on the server
        appEvent.votedStations
            .flatMapSingle(stationVoteUseCase::execute)
            .flatMapSingle {
                Single.just(
                    if (it.ok) {
                        AppNotification(messages["vote.ok"], FontAwesome.Glyph.CHECK)
                    } else {
                        AppNotification(messages["vote.error"], FontAwesome.Glyph.WARNING)
                    }
                )
            }.subscribe(appEvent.appNotification)
    }

    private fun show(stations: List<Station>) {
        if (stations.isEmpty()) {
            stateProperty.value = StationsState.NoStations
        } else {
            stateProperty.value = StationsState.Fetched(stations)
            item = Stations(stations.asObservable())
        }
    }

    private fun search() {
        if (searchViewModel.queryBinding.value.length <= 2) {
            stateProperty.value = StationsState.ShortQuery
        } else {
            searchViewModel
                .search()
                .subscribe(::show, ::handleError)
        }
    }

    private fun handleError(throwable: Throwable) {
        stateProperty.value = StationsState.Error(throwable.toString())
    }

    private fun handleNewLibraryState(newState: LibraryState) {
        when (newState) {
            is LibraryState.SelectedCountry -> getStationsByCountryUseCase
                .execute(newState.country)
                .subscribe(::show, ::handleError)

            is LibraryState.Favourites -> show(favouritesViewModel.stationsProperty)
            is LibraryState.Popular -> getPopularStationsUseCase
                .execute(Unit)
                .subscribe(::show, ::handleError)

            is LibraryState.Trending -> getTrendingStationsUseCase
                .execute(Unit)
                .subscribe(::show, ::handleError)

            is LibraryState.Search -> search()
        }
    }
}
