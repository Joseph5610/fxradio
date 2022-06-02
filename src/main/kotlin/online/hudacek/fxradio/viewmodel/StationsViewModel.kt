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

import io.reactivex.Single
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.usecase.GetStationsByCountryUseCase
import online.hudacek.fxradio.usecase.GetTrendingStationsUseCase
import online.hudacek.fxradio.usecase.GetTopVotedStationsUseCase
import online.hudacek.fxradio.usecase.VoteUseCase
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

    private val getTopVotedStationsUseCase: GetTopVotedStationsUseCase by inject()
    private val getTrendingStationsUseCase: GetTrendingStationsUseCase by inject()
    private val getStationsByCountryUseCase: GetStationsByCountryUseCase by inject()
    private val voteUseCase: VoteUseCase by inject()

    val stationsProperty = bind(Stations::stations) as ListProperty

    init {
        appEvent.refreshLibrary
                .filter { it == libraryViewModel.stateProperty.value }
                .subscribe(::handleNewLibraryState)

        libraryViewModel
                .stateObservableChanges
                .subscribe(::handleNewLibraryState)

        searchViewModel
                .queryChanges
                .subscribe { search() }

        //Increase vote count on the server
        appEvent.addVote
                .flatMapSingle(voteUseCase::execute)
                .flatMapSingle {
                    Single.just(if (it.ok) {
                        AppNotification(messages["vote.ok"], FontAwesome.Glyph.CHECK)
                    } else {
                        AppNotification(messages["vote.error"], FontAwesome.Glyph.WARNING)
                    })
                }
                .subscribe(appEvent.appNotification)
    }

    fun show(stations: List<Station>) {
        if (stations.isEmpty()) {
            stateProperty.value = StationsState.NoStations
        } else {
            stateProperty.value = StationsState.Fetched(stations)
            item = Stations(stations.asObservable())
        }
    }

    fun search() {
        if (searchViewModel.queryBinding.value.length <= 2) {
            stateProperty.value = StationsState.ShortQuery
        } else {
            searchViewModel
                    .search()
                    .subscribe(::show, ::handleError)
        }
    }

    private fun handleError(throwable: Throwable) {
        stateProperty.value = StationsState.Error(throwable.localizedMessage)
    }

    fun handleNewLibraryState(newState: LibraryState) {
        when (newState) {
            is LibraryState.SelectedCountry -> getStationsByCountryUseCase
                    .execute(newState.country)
                    .subscribe(::show, ::handleError)
            is LibraryState.Favourites -> show(favouritesViewModel.stationsProperty)
            is LibraryState.TopVotedStations -> getTopVotedStationsUseCase
                    .execute(Unit)
                    .subscribe(::show, ::handleError)
            is LibraryState.TrendingStations -> getTrendingStationsUseCase
                    .execute(Unit)
                    .subscribe(::show, ::handleError)
            is LibraryState.Search -> search()
            else -> {
                show(observableListOf(Station.dummy))
            }
        }
    }
}

