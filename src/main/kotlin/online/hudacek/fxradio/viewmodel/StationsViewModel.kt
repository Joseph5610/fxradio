/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

