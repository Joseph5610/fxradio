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

package online.hudacek.fxradio.ui.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

private val logger = KotlinLogging.logger {}

enum class StationsViewState {
    Loaded, Error, Loading, Empty, ShortQuery
}

class Stations(stations: ObservableList<Station> = observableListOf(),
               viewState: StationsViewState = StationsViewState.Empty) {
    var stations: ObservableList<Station> by property(stations)
    var viewState: StationsViewState by property(viewState)
}

/**
 * Stations view model
 * -------------------
 * Holds information about currently shown
 * stations inside [online.hudacek.fxradio.ui.view.stations.StationsDataGridView]
 */
class StationsViewModel : ItemViewModel<Stations>(Stations()) {

    private val historyViewModel: HistoryViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    val stationsProperty = bind(Stations::stations) as ListProperty
    val viewStateProperty = bind(Stations::viewState) as ObjectProperty

    //Retrieves top voted stations list from endpoint
    private val topStations: Single<List<Station>> = StationsApi.service
            .getTopStations()
            .compose(applySchedulers())

    init {
        selectedLibraryViewModel.itemProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .subscribe {
                    viewStateProperty.value = StationsViewState.Loading
                }

        viewStateProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .filter { it == StationsViewState.Loading }
                .map { selectedLibraryViewModel.itemProperty.value }
                .subscribe {
                    when (it.type) {
                        LibraryType.Country -> stationsByCountry(it.libraryOption)
                        LibraryType.Favourites -> show(favouritesViewModel.stationsProperty)
                        LibraryType.History -> show(historyViewModel.stationsProperty)
                        LibraryType.TopStations -> topStations.subscribe(::show, ::handleError)
                        LibraryType.Search -> search()
                    }
                }

        //Refresh search on query change
        searchViewModel.queryChanges
                .subscribe {
                    //It can happen that the current library is not search when the query changes
                    selectedLibraryViewModel.item = SelectedLibrary(LibraryType.Search)
                    searchViewModel.commit()
                    search() //Perform the search call again
                }
    }

    private fun search() {
        if (searchViewModel.queryProperty.length() <= 2) {
            viewStateProperty.value = StationsViewState.ShortQuery
        } else {
            if (searchViewModel.searchByTagProperty.value) {
                searchViewModel.searchByTagSingle.subscribe(::show, ::handleError)
            } else {
                searchViewModel.searchByNameSingle.subscribe(::show, ::handleError)
            }
        }
    }

    //retrieve all stations from given country from endpoint
    private fun stationsByCountry(country: String): Disposable = StationsApi.service
            .getStationsByCountry(CountriesBody(), country)
            .compose(applySchedulers())
            .subscribe(::show, ::handleError)

    fun show(stations: List<Station>) {
        stationsProperty.value = stations.asObservable()
        viewStateProperty.value = if (stations.isEmpty()) {
            StationsViewState.Empty
        } else {
            StationsViewState.Loaded
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        viewStateProperty.value = StationsViewState.Error
    }
}

