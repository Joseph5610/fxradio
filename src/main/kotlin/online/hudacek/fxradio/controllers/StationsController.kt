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

package online.hudacek.fxradio.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.viewmodel.StationsHistoryModel
import online.hudacek.fxradio.viewmodel.StationsModel
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.views.StationsView
import tornadofx.*

class StationsController : Controller() {

    private val logger = KotlinLogging.logger {}

    private val stationsView: StationsView by inject()
    private val stationsHistory: StationsHistoryModel by inject()
    private val stationsModel: StationsModel by inject()

    private val stationsApi: StationsApi
        get() = StationsApi.client

    //retrieve favourites from DB
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

    //retrieve all stations from given country from endpoint
    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                stationsView.showStations()
                stationsModel.stationsProperty.set(it.asObservable())
            }, ::handleError)

    //search for station name on endpoint
    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                if (it.isEmpty()) {
                    stationsView.showNoResults(name)
                } else {
                    stationsView.showStations()
                    stationsModel.stationsProperty.set(it.asObservable())
                }
            }, ::handleError)

    //retrieve history list
    fun getHistory() = stationsHistory.stations.let {
        if (it.isEmpty()) {
            stationsView.showNoResults()
        } else {
            stationsView.showStations()
            stationsModel.stationsProperty.set(it)
        }
    }

    //retrieve top voted stations list from endpoint
    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                stationsView.showStations()
                stationsModel.stationsProperty.set(it.asObservable())
            }, ::handleError)


    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        stationsView.showError()
    }
}