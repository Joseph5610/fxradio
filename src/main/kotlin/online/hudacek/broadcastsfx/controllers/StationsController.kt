package online.hudacek.broadcastsfx.controllers

import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.views.StationsView
import tornadofx.Controller
import tornadofx.asObservable

class StationsController : Controller() {

    private val stationsView: StationsView by inject()
    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe({ result ->
                stationsView.showDataGrid(result.asObservable())
            }, {
                stationsView.showNotification()
            })

    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe({ result ->
                if (result.isEmpty()) {
                    stationsView.showNoResults()
                } else {
                    stationsView.showDataGrid(result.asObservable())
                }
            }, {
                stationsView.showNotification()
            })

    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe({ result ->
                stationsView.showDataGrid(result.asObservable())
            }, {
                stationsView.showNotification()
            })
}