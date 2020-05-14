package online.hudacek.broadcastsfx.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.StationHistoryModel
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.views.StationsView
import tornadofx.*

class StationsController : Controller() {

    private val stationsView: StationsView by inject()
    private val stationsHistory: StationHistoryModel by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    fun getFavourites() {
        Station.favourites()
                .observeOnFx()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.isEmpty()) {
                        stationsView.showNoResults()
                    } else {
                        stationsView.showDataGrid(it)
                    }
                }, {
                    stationsView.showError(it)
                })
        stationsView.setContentName(LibraryType.Favourites)
    }

    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.setContentName(LibraryType.Country, country)
                stationsView.showDataGrid(result)
            }, {
                stationsView.showError(it)
            })

    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                if (result.isEmpty()) {
                    stationsView.showNoResults(name)
                } else {
                    stationsView.setContentName(LibraryType.Search, name)
                    stationsView.showDataGrid(result)
                }
            }, {
                stationsView.showError(it)
            })

    fun getHistory() {
        val historyList = stationsHistory.stations.value.distinct()
        if (historyList.isEmpty()) {
            stationsView.showNoResults()
        } else {
            stationsView.showDataGrid(historyList)
        }
        stationsView.setContentName(LibraryType.History)
    }

    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.setContentName(LibraryType.TopStations)
                stationsView.showDataGrid(result)
            }, {
                stationsView.showError(it)
            })
}