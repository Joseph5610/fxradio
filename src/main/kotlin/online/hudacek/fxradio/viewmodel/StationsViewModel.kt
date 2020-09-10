package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleBooleanProperty
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

class StationsModel {
    val stations = observableListOf(Station.stub())
}

class StationsViewModel : ItemViewModel<StationsModel>() {

    val stationsProperty = bind(StationsModel::stations) as ListProperty
    val errorVisible = SimpleBooleanProperty(false)

    private val stationsApi: StationsApi
        get() = StationsApi.client

    //retrieve favourites from DB
    fun getFavourites(): Disposable = Station.favourites()
            .observeOnFx()
            .subscribeOn(Schedulers.io())
            .subscribe({
                errorVisible.value = false
                stationsProperty.setAll(it)
            }, ::handleError)

    //retrieve all stations from given country from endpoint
    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                errorVisible.value = false
                stationsProperty.setAll(it)
            }, ::handleError)

    //search for station name on endpoint
    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                errorVisible.value = false
                stationsProperty.setAll(it)
            }, ::handleError)

    //retrieve history list
    fun getHistory() {

    }

    //retrieve top voted stations list from endpoint
    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                errorVisible.value = false
                stationsProperty.setAll(it)
            }, ::handleError)


    private fun handleError(throwable: Throwable) {
        errorVisible.value = true
    }
}