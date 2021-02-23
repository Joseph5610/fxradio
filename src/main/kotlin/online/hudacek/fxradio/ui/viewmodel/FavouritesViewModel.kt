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

import io.reactivex.subjects.BehaviorSubject
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationPaneEvent
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.db.Tables
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property
import java.text.MessageFormat

private val logger = KotlinLogging.logger {}

class FavouritesModel(stations: ObservableList<Station> = observableListOf()) {
    var stations: ObservableList<Station> by property(stations)
}

/**
 * Favourites view model
 * -------------------
 * Holds information about last favourites stations
 * shows in [online.hudacek.fxradio.ui.view.stations.StationsDataGridView] and in MenuBar
 */
class FavouritesViewModel : ItemViewModel<FavouritesModel>(FavouritesModel()) {
    val stationsProperty = bind(FavouritesModel::stations) as ListProperty

    val addFavourite = BehaviorSubject.create<Station>()
    val removeFavourite = BehaviorSubject.create<Station>()
    val cleanupFavourites = BehaviorSubject.create<Unit>()

    init {
        addFavourite
                .filter { it.isValid() && !stationsProperty.contains(it) }
                .flatMapSingle { Tables.favourites.insert(it) }
                .subscribe({
                    val addStr = MessageFormat.format(messages["menu.station.favouriteAdded"], it.name)
                    stationsProperty.add(it)
                    fire(NotificationPaneEvent(addStr, FontAwesome.Glyph.CHECK))
                }, {
                    fire(NotificationPaneEvent(messages["menu.station.favouriteAdded.error"]))
                })

        cleanupFavourites
                .doOnError { logger.error(it) { "Cannot perform DB cleanup!" } }
                .flatMapSingle { Tables.favourites.delete() }
                .subscribe {
                    item = FavouritesModel()
                }

        removeFavourite
                .flatMapSingle { Tables.favourites.remove(it) }
                .subscribe({
                    val removeStr = MessageFormat.format(messages["menu.station.favouriteRemoved"], it.name)
                    stationsProperty.remove(it)
                    fire(NotificationPaneEvent(removeStr, FontAwesome.Glyph.CHECK))
                }, {
                    fire(NotificationPaneEvent(messages["menu.station.favouriteRemove.error"]))
                })
    }
}