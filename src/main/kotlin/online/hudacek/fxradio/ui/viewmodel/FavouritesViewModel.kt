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

package online.hudacek.fxradio.ui.viewmodel

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.formatted
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

private val logger = KotlinLogging.logger {}

class Favourites(stations: ObservableList<Station> = observableListOf()) {
    var stations: ObservableList<Station> by property(stations)
}

/**
 * Favourites view model
 * -------------------
 * Holds information about last favourites stations
 * shows in [online.hudacek.fxradio.ui.view.stations.StationsDataGridView] and in MenuBar
 */
class FavouritesViewModel : ItemViewModel<Favourites>(Favourites()) {
    private val appEvent: AppEvent by inject()

    val stationsProperty = bind(Favourites::stations) as ListProperty

    init {
        appEvent.addFavourite
                .filter { it.isValid() && !stationsProperty.contains(it) }
                .flatMapSingle { Tables.favourites.insert(it) }
                .subscribe({
                    stationsProperty.add(it)
                    appEvent.appNotification.onNext(
                            AppNotification(messages["menu.station.favouriteAdded"].formatted(it.name),
                                    FontAwesome.Glyph.CHECK))
                }, {
                    appEvent.appNotification.onNext(
                            AppNotification(messages["menu.station.favouriteAdded.error"],
                                    FontAwesome.Glyph.WARNING))
                })

        appEvent.cleanupFavourites
                .doOnError { logger.error(it) { "Cannot perform DB cleanup!" } }
                .flatMapSingle { Tables.favourites.removeAll() }
                .subscribe {
                    item = Favourites()
                }

        appEvent.removeFavourite
                .flatMapSingle { Tables.favourites.remove(it) }
                .subscribe({
                    stationsProperty.remove(it)
                    appEvent.appNotification.onNext(
                            AppNotification(messages["menu.station.favouriteRemoved"].formatted(it.name),
                                    FontAwesome.Glyph.CHECK))
                }, {
                    appEvent.appNotification.onNext(
                            AppNotification(messages["menu.station.favouriteRemove.error"],
                                    FontAwesome.Glyph.WARNING))
                })
    }
}