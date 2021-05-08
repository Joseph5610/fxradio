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
import mu.KotlinLogging
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.formatted
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

    val stationsProperty = bind(Favourites::stations) as ListProperty

    init {
        Tables.favourites
                .selectAll()
                .subscribe {
                    stationsProperty += it
                }

        appEvent.addFavourite
                .filter { it.isValid() && it !in stationsProperty }
                .flatMapSingle { Tables.favourites.insert(it) }
                .flatMapSingle {
                    stationsProperty += it
                    Single.just(AppNotification(messages["menu.station.favouriteAdded"].formatted(it.name),
                            FontAwesome.Glyph.CHECK))
                }.subscribe(appEvent.appNotification)

        appEvent.removeFavourite
                .flatMapSingle { Tables.favourites.remove(it) }
                .flatMapSingle {
                    stationsProperty.remove(it)
                    Single.just(AppNotification(messages["menu.station.favouriteRemoved"].formatted(it.name),
                            FontAwesome.Glyph.CHECK))
                }.subscribe(appEvent.appNotification)
    }

    fun cleanupFavourites(): Disposable = Tables.favourites
            .removeAll()
            .subscribe({
                item = Favourites()
            }, {
                logger.error(it) { "Cannot perform DB cleanup!" }
            })
}