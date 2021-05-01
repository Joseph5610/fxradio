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
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.usecase.GetStationsByCountryUseCase
import online.hudacek.fxradio.usecase.GetTopStationsUseCase
import online.hudacek.fxradio.usecase.VoteUseCase
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

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

    private val getTopStationsUseCase: GetTopStationsUseCase by inject()
    private val getStationsByCountryUseCase: GetStationsByCountryUseCase by inject()
    private val voteUseCase: VoteUseCase by inject()

    val stationsProperty = bind(Stations::stations) as ListProperty

    init {
        //Refresh search on query change
        searchViewModel.queryObservable
                .subscribe {
                    //It can happen that the current library is not search when the query changes
                    libraryViewModel.stateProperty.value = LibraryState.Search
                    searchViewModel.commit()
                    search() //Perform the search call again
                }

        appEvent.refreshLibrary
                .filter { it == libraryViewModel.stateProperty.value }
                .subscribe(::handleNewLibraryState)

        libraryViewModel
                .stateObservableChanges()
                .subscribe(::handleNewLibraryState)

        //Increase vote count on the server
        appEvent.vote
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

    private fun search() {
        if (searchViewModel.queryProperty.length() <= 2) {
            stateProperty.value = StationsState.ShortQuery
        } else {
            if (searchViewModel.searchByTagProperty.value) {
                searchViewModel.searchByTag()
                        .subscribe(::show, ::handleError)
            } else {
                searchViewModel.searchByName()
                        .subscribe(::show, ::handleError)
            }
        }
    }

    fun show(stations: List<Station>) {
        if (stations.isNullOrEmpty()) {
            stateProperty.value = StationsState.NoStations
        } else {
            stateProperty.value = StationsState.Fetched(stations)
            item = Stations(stations.asObservable())
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
            is LibraryState.TopStations -> getTopStationsUseCase
                    .execute(Unit)
                    .subscribe(::show, ::handleError)
            is LibraryState.Search -> search()
            else -> {
                show(observableListOf(Station.dummy))
            }
        }
    }
}

