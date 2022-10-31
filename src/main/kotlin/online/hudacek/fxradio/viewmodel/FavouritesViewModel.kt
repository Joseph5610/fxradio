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

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.usecase.FavouriteAddUseCase
import online.hudacek.fxradio.usecase.FavouriteRemoveUseCase
import online.hudacek.fxradio.usecase.FavouriteSetUseCase
import online.hudacek.fxradio.usecase.FavouriteUpdateUseCase
import online.hudacek.fxradio.usecase.FavouritesClearUseCase
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
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
    private val favouriteSetUseCase: FavouriteSetUseCase by inject()

    val stationsProperty = bind(Favourites::stations) as ListProperty

    init {
        favouriteSetUseCase.execute(Unit).subscribe { stationsProperty.add(it) }

        appEvent.addFavourite
            .filter { it.isValid() && it !in stationsProperty }
            .flatMapSingle { favouriteAddUseCase.execute(it) }
            .flatMapSingle {
                stationsProperty += it
                Single.just(
                    AppNotification(
                        messages["menu.station.favouriteAdded"].formatted(it.name),
                        FontAwesome.Glyph.CHECK
                    )
                )
            }.subscribe(appEvent.appNotification)

        appEvent.removeFavourite
            .flatMapSingle { favouriteRemoveUseCase.execute(it) }
            .flatMapSingle {
                stationsProperty -= it
                Single.just(
                    AppNotification(
                        messages["menu.station.favouriteRemoved"].formatted(it.name),
                        FontAwesome.Glyph.CHECK
                    )
                )
            }.subscribe(appEvent.appNotification)
    }

    fun cleanupFavourites(): Disposable = cleanFavouritesClearUseCase.execute(Unit)
        .subscribe({
            item = Favourites()
        }, {
            logger.error(it) { "Cannot remove favourites" }
        })

    override fun onCommit() {
        favouritesUpdateUseCase.execute(stationsProperty).subscribe({

        }, {
            // rollback viewmodel to previous state when update failed
            rollback()
        })
    }
}
