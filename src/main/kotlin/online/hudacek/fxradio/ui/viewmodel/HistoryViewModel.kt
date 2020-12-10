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
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.Database
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.property

class HistoryModel(stations: ObservableList<Station> = observableListOf()) {
    val stations: ObservableList<Station> by property(stations)
}

/**
 * Stations History view model
 * -------------------
 * Holds information about last 10 played stations
 * shows in [online.hudacek.fxradio.views.stations.StationsDataGridView] and in MenuBar
 */
class HistoryViewModel : ItemViewModel<HistoryModel>() {
    val stationsProperty = bind(HistoryModel::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValid()) return
        with(stationsProperty) {
            if (!contains(station)) {
                add(station)
                Database.history
                        .insert(station)
                        .subscribe()
            }
        }
    }

    fun cleanup() {
        item = HistoryModel()
        Database.history
                .delete()
                .subscribe()
    }
}