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

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.Database
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

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

    fun add(station: Station) {
        if (!station.isValid()) return
        if (!stationsProperty.contains(station)) {
            Database.favourites
                    .insert(station)
                    .subscribe({
                        stationsProperty.add(it)
                        fire(NotificationEvent(messages["menu.station.favourite.added"], FontAwesome.Glyph.CHECK))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.added.error"]))
                    })
        }
    }

    fun cleanup(): Disposable = Database.favourites
            .delete()
            .subscribe({
                item = FavouritesModel()
            }, {
                logger.error(it) { "Cannot perform DB cleanup!" }
            })

    fun remove(station: Station): Disposable = Database.favourites
            .remove(station)
            .subscribe({
                stationsProperty.remove(station)
                fire(NotificationEvent(messages["menu.station.favourite.removed"], FontAwesome.Glyph.CHECK))
            }, {
                fire(NotificationEvent(messages["menu.station.favourite.remove.error"]))
            })
}