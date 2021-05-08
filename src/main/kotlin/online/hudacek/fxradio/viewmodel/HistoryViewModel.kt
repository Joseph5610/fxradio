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

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.db.Tables
import tornadofx.observableListOf
import tornadofx.property

private val logger = KotlinLogging.logger {}

class History(stations: ObservableList<Station> = observableListOf()) {
    var stations: ObservableList<Station> by property(stations)
}

/**
 * Holds information about last played stations
 * shows in [online.hudacek.fxradio.ui.view.stations.StationsDataGridView] and in MenuBar
 */
class HistoryViewModel : BaseViewModel<History>(History()) {

    val stationsProperty = bind(History::stations) as ListProperty

    init {
        Tables.history
                .selectAll()
                .subscribe {
                    stationsProperty += it
                }

        //Add currently listened station to history
        appEvent.addToHistory
                //Add only valid stations not already present in history
                .filter { it.isValid() }
                .doOnError { logger.error(it) { "Error adding station to history!" } }
                .flatMapSingle { Tables.history.insert(it) }
                .subscribe {
                    stationsProperty += it
                }
    }

    fun cleanupHistory() = Tables.history
            .removeAll()
            .toObservable()
            .doOnError { logger.error(it) { "Cannot perform DB cleanup!" } }
            .doOnEach { item = History() }
            .map { LibraryState.History }
            .subscribe(appEvent.refreshLibrary)
}