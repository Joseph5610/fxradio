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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.SearchByTagBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

private val logger = KotlinLogging.logger {}

enum class StationsViewState {
    Normal, Error, Loading, Empty, ShortQuery
}

class StationsModel(stations: ObservableList<Station> = observableListOf(),
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
class StationsViewModel : ItemViewModel<StationsModel>(StationsModel()) {

    private val historyViewModel: HistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    val stationsProperty = bind(StationsModel::stations) as ListProperty
    val viewStateProperty = bind(StationsModel::viewState) as ObjectProperty

    private val viewStateChanges: Observable<StationsViewState> = viewStateProperty
            .toObservableChangesNonNull()
            .map { it.newVal }

    init {
        libraryViewModel.selectedPropertyChanges
                .subscribe {
                    viewStateProperty.value = StationsViewState.Loading
                    logger.debug { "selectedProperty changed: $it" }
                }

        viewStateChanges
                .filter { it == StationsViewState.Loading }
                .subscribe {
                    with(libraryViewModel.selectedProperty.value) {
                        when (type) {
                            LibraryType.Country -> stationsByCountry(params)
                            LibraryType.Favourites -> show(favouritesViewModel.stationsProperty)
                            LibraryType.History -> show(historyViewModel.stationsProperty)
                            LibraryType.TopStations -> topStations.subscribe(::show, ::handleError)
                            LibraryType.Search -> search()
                            LibraryType.SearchByTag -> search(useTag = true)
                        }
                    }
                }

        //Refresh search on query change
        libraryViewModel.searchQueryProperty.onChange {
            with(libraryViewModel.selectedProperty.value) {
                if (type == LibraryType.Search)
                    search()
                else if (type == LibraryType.SearchByTag)
                    search(useTag = true)
            }
        }
    }

    //retrieve top voted stations list from endpoint
    private val topStations: Single<List<Station>> = StationsApi.service
            .getTopStations()
            .compose(applySchedulers())

    //retrieve all stations from given country from endpoint
    private fun stationsByCountry(country: String): Disposable = StationsApi.service
            .getStationsByCountry(CountriesBody(), country)
            .compose(applySchedulers())
            .subscribe(::show, ::handleError)

    private fun search(useTag: Boolean = false) {
        val query = libraryViewModel.searchQueryProperty.value
        if (query.length <= 2) {
            viewStateProperty.value = StationsViewState.ShortQuery
        } else {
            if (useTag) {
                StationsApi.service
                        .searchStationByTag(SearchByTagBody(query))
                        .compose(applySchedulers())
                        .subscribe(::show, ::handleError)
            } else {
                StationsApi.service
                        .searchStationByName(SearchBody(query))
                        .compose(applySchedulers())
                        .subscribe(::show, ::handleError)
            }
        }
    }

    private fun show(stations: List<Station>) {
        stationsProperty.value = stations.asObservable()
        viewStateProperty.value = if (stations.isEmpty()) {
            StationsViewState.Empty
        } else {
            StationsViewState.Normal
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        viewStateProperty.value = StationsViewState.Error
    }
}

