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

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.apiclient.radiobrowser.model.StatsResult
import online.hudacek.fxradio.usecase.GetStatsUseCase
import tornadofx.observableListOf
import tornadofx.property

sealed class StatsState {
    object Loading : StatsState()
    data class Fetched(val stats: StatsResult) : StatsState()
    data class Error(val cause: String) : StatsState()
}

class Stats(stats: ObservableList<Pair<String, String>> = observableListOf()) {
    var stats: ObservableList<Pair<String, String>> by property(stats)
}

/**
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.ui.fragment.StatsFragment]
 */
class StatsViewModel : BaseStateViewModel<Stats, StatsState>(Stats()) {

    private val getStatsUseCase: GetStatsUseCase by inject()

    val statsProperty = bind(Stats::stats) as ListProperty

    fun fetchStats() {
        stateProperty.value = StatsState.Loading
        getStatsUseCase.execute(Unit).subscribe({
            stateProperty.value = StatsState.Fetched(it)
        }, {
            stateProperty.value = StatsState.Error(it.localizedMessage)
        })
    }

    override fun onNewState(newState: StatsState) {
        if (newState is StatsState.Fetched) {
            item = Stats(
                observableListOf(
                    Pair("stats.status", newState.stats.status),
                    Pair("stats.software_version", newState.stats.software_version),
                    Pair("stats.stations", newState.stats.stations),
                    Pair("stats.stations_broken", newState.stats.stations_broken),
                    Pair("stats.tags", newState.stats.tags),
                    Pair("stats.countries", newState.stats.countries.toString()),
                    Pair("stats.languages", newState.stats.languages.toString())

                )
            )
        }
    }
}
