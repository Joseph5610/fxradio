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

package online.hudacek.fxradio.viewmodel

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.stations.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.usecase.FavouriteAddUseCase
import online.hudacek.fxradio.usecase.FavouriteRemoveUseCase
import online.hudacek.fxradio.usecase.FavouriteSetUseCase
import online.hudacek.fxradio.usecase.FavouritesClearUseCase
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

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
    private val favouriteSetUseCase: FavouriteSetUseCase by inject()

    val stationsProperty = bind(Favourites::stations) as ListProperty

    init {
        favouriteSetUseCase.execute(stationsProperty)

        appEvent.addFavourite
                .filter { it.isValid() && it !in stationsProperty }
                .flatMapSingle { favouriteAddUseCase.execute(it) }
                .flatMapSingle {
                    stationsProperty += it
                    Single.just(AppNotification(messages["menu.station.favouriteAdded"].formatted(it.name),
                            FontAwesome.Glyph.CHECK))
                }.subscribe(appEvent.appNotification)

        appEvent.removeFavourite
                .flatMapSingle { favouriteRemoveUseCase.execute(it) }
                .flatMapSingle {
                    stationsProperty -= it
                    Single.just(AppNotification(messages["menu.station.favouriteRemoved"].formatted(it.name),
                            FontAwesome.Glyph.CHECK))
                }.subscribe(appEvent.appNotification)
    }

    fun cleanupFavourites(): Disposable = cleanFavouritesClearUseCase.execute(this)
}