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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

enum class StatsViewState {
    Loading, Loaded, Error
}

class Stats(stats: ObservableList<Pair<String, String>> = observableListOf(),
            viewState: StatsViewState = StatsViewState.Loaded) {
    var stats: ObservableList<Pair<String, String>> by property(stats)
    var viewState: StatsViewState by property(viewState)
}

/**
 * Stats view model
 * -------------------
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.ui.modal.StatsFragment]
 */
class StatsViewModel : ItemViewModel<Stats>(Stats()) {
    val statsProperty = bind(Stats::stats) as ListProperty<Pair<String, String>>
    val viewStateProperty = bind(Stats::viewState) as ObjectProperty

    init {
        viewStateProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .filter { it == StatsViewState.Loading }
                .flatMapSingle {
                    StationsApi.service
                            .getStats()
                            .compose(applySchedulers())
                }.subscribe({
                    val stringValueMap = observableMapOf(
                            "stats.status" to it.status,
                            "stats.apiVersion" to it.software_version,
                            "stats.supportedVersion" to it.supported_version,
                            "stats.stations" to it.stations,
                            "stats.countries" to it.countries,
                            "stats.brokenStations" to it.stations_broken,
                            "stats.tags" to it.tags
                    )
                    item = Stats(stringValueMap
                            .toList()
                            .asObservable(), StatsViewState.Loaded)
                }, {
                    item = Stats(viewState = StatsViewState.Error)
                })
    }
}