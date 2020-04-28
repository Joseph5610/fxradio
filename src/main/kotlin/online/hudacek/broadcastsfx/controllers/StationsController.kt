package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.model.rest.HideBrokenBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.Controller

class StationsController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    fun getStationsByCountry(country: String): Observable<List<Station>> = stationsApi
            .getStationsByCountry(HideBrokenBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())

    fun searchStations(name: String): Observable<List<Station>> = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())

    fun getTopStations(): Observable<List<Station>> = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
}