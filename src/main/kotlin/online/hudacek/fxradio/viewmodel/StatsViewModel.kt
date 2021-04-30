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
import online.hudacek.fxradio.api.model.StatsResult
import online.hudacek.fxradio.usecase.GetStatsUseCase
import tornadofx.observableListOf
import tornadofx.property

sealed class StatsState {
    object Loading : StatsState()
    data class Fetched(val stats: StatsResult) : StatsState()
    object Error : StatsState()
}

class Stats(stats: ObservableList<Pair<String, String>> = observableListOf()) {
    var stats: ObservableList<Pair<String, String>> by property(stats)
}

/**
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.ui.modal.StatsFragment]
 */
class StatsViewModel : BaseViewModel<Stats, StatsState>(Stats()) {

    private val getStatsUseCase: GetStatsUseCase by inject()

    val statsProperty = bind(Stats::stats) as ListProperty

    fun fetchStats() {
        stateProperty.value = StatsState.Loading
        getStatsUseCase.execute(Unit).subscribe({
            stateProperty.value = StatsState.Fetched(it)
        }, {
            stateProperty.value = StatsState.Error
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
                            Pair("stats.tags", newState.stats.tags)
                    ))
        }
    }
}


