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

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.util.applySchedulers
import online.hudacek.fxradio.util.applySchedulersSingle
import tornadofx.move
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

        // Add currently listened station to history
        appEvent.stationsHistory
            .compose(applySchedulers())
            // Add only valid stations
            .filter { it.isValid() }
            .doOnError { logger.error(it) { "Exception when adding station to history!" } }
            .flatMapSingle {
                if (it in stationsProperty) {
                    stationsProperty.move(it, 0)
                    Tables.history.remove(it).subscribe()
                } else {
                    stationsProperty.add(0, it)
                }
                Tables.history.insert(it)
            }.subscribe(appEvent.historyUpdated)
    }

    fun cleanupHistory(): Disposable = Tables.history
        .removeAll()
        .compose(applySchedulersSingle())
        .subscribe({
            item = History()
            commit()
        }, {
            logger.error(it) { "Failed to remove history" }
        })
}
