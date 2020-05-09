package online.hudacek.broadcastsfx.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.views.StationsView
import tornadofx.*

class StationsController : Controller() {

    private val stationsView: StationsView by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    fun getFavourites() {
        val list = observableListOf<Station>()
        Station.favourites()
                .observeOnFx()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    list.add(it.last())
                }
        stationsView.showDataGrid(list)
    }

    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.showDataGrid(result.asObservable())
            }, {
                stationsView.showError()
            })

    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                if (result.isEmpty()) {
                    stationsView.showNoResults(name)
                } else {
                    stationsView.showDataGrid(result.asObservable())
                }
            }, {
                stationsView.showError()
            })

    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.showDataGrid(result.asObservable())
            }, {
                stationsView.showError()
            })
}