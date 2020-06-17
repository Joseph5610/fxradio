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

package online.hudacek.broadcastsfx.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.model.StationsHistoryModel
import online.hudacek.broadcastsfx.model.StationsModel
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.views.StationsView
import tornadofx.*

class StationsController : Controller() {

    private val logger = KotlinLogging.logger {}

    private val stationsView: StationsView by inject()
    private val stationsHistory: StationsHistoryModel by inject()
    private val stationsModel: StationsModel by inject()

    private val stationsApi: StationsApi
        get() = StationsApi.client

    fun getFavourites(): Disposable = Station.favourites()
            .observeOnFx()
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it.isEmpty()) {
                    stationsView.showNoResults()
                } else {
                    stationsView.showStations()
                    stationsModel.stationsProperty.set(it.asObservable())
                }
            }, ::handleError)

    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.showStations()
                stationsModel.stationsProperty.set(result.asObservable())
            }, ::handleError)

    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                if (result.isEmpty()) {
                    stationsView.showNoResults(name)
                } else {
                    stationsView.showStations()
                    stationsModel.stationsProperty.set(result.asObservable())
                }
            }, ::handleError)

    fun getHistory() = stationsHistory.stations.let {
        if (it.isEmpty()) {
            stationsView.showNoResults()
        } else {
            stationsView.showStations()
            stationsModel.stationsProperty.set(it)
        }
    }

    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.showStations()
                stationsModel.stationsProperty.set(result.asObservable())
            }, ::handleError)

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        stationsView.showError()
    }
}