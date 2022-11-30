/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.usecase.favourites.*
import tornadofx.observableListOf
import tornadofx.property

private val logger = KotlinLogging.logger {}

class Favourites(stations: ObservableList<Station> = observableListOf()) {
    var stations: ObservableList<Station> by property(stations)
}

/**
 * Holds information about last favourites stations
 * shows in [online.hudacek.fxradio.ui.view.stations.StationsDataGridView] and in MenuBar
 */
class FavouritesViewModel : BaseViewModel<Favourites>(Favourites()) {

    private val favouriteAddUseCase: FavouriteAddUseCase by inject()
    private val favouriteRemoveUseCase: FavouriteRemoveUseCase by inject()
    private val cleanFavouritesClearUseCase: FavouritesClearUseCase by inject()
    private val favouritesUpdateUseCase: FavouriteUpdateUseCase by inject()
    private val favouritesGetUseCase: FavouritesGetUseCase by inject()

    val stationsProperty = bind(Favourites::stations) as ListProperty

    val stationsObservable: Observable<ObservableList<Station>> = stationsProperty.toObservable()

    init {
        favouritesGetUseCase.execute(Unit).subscribe {
            stationsProperty.add(it)
        }
    }

    fun addFavourite(station: Station): Disposable = favouriteAddUseCase.execute(station).subscribe({
        stationsProperty += it
    }, {
        logger.error(it) { "Cannot add ${station.uuid}" }
    })

    fun removeFavourite(station: Station): Disposable = favouriteRemoveUseCase.execute(station).subscribe({
        stationsProperty -= it
    }, {
        logger.error(it) { "Cannot remove ${station.uuid}" }
    })

    fun cleanupFavourites(): Disposable = cleanFavouritesClearUseCase.execute(Unit)
        .subscribe({
            item = Favourites()
        }, {
            logger.error(it) { "Cannot remove favourites" }
        })

    override fun onCommit() {
        favouritesUpdateUseCase.execute(stationsProperty).subscribe({}, {
            // Rollback ViewModel to previous state when update failed
            rollback()
        })
    }
}
