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
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

enum class StatsViewState {
    Loading, Normal, Error
}

class StatsModel(map: ObservableList<Pair<String, String>> = observableListOf(),
                 viewState: StatsViewState = StatsViewState.Loading) {
    var stats: ObservableList<Pair<String, String>> by property(map)
    var viewState: StatsViewState by property(viewState)
}

/**
 * Stats view model
 * -------------------
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.ui.fragment.StatsFragment]
 */
class StatsViewModel : ItemViewModel<StatsModel>(StatsModel()) {
    val statsProperty = bind(StatsModel::stats) as ListProperty<Pair<String, String>>
    val viewStateProperty = bind(StatsModel::viewState) as ObjectProperty

    fun getStats(): Disposable {
        viewStateProperty.value = StatsViewState.Loading
        return StationsApi.service.getStats()
                .compose(applySchedulers())
                .subscribe({
                    val statsPair = observableListOf(
                            Pair(messages["stats.status"], it.status),
                            Pair(messages["stats.apiVersion"], it.software_version),
                            Pair(messages["stats.supportedVersion"], it.supported_version),
                            Pair(messages["stats.stations"], it.stations),
                            Pair(messages["stats.countries"], it.countries),
                            Pair(messages["stats.brokenStations"], it.stations_broken),
                            Pair(messages["stats.tags"], it.tags)
                    )
                    item = StatsModel(statsPair, StatsViewState.Normal)
                }, {
                    item = StatsModel(viewState = StatsViewState.Error)
                })
    }
}