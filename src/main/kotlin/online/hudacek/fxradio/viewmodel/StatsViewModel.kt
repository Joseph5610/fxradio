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

import io.reactivex.disposables.Disposable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

class StatsModel(map: ObservableList<Pair<String, String>> = observableListOf()) {
    val stats: ObservableList<Pair<String, String>> by property(map)
    val isError by booleanProperty()
}

/**
 * Stats view model
 * -------------------
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.fragments.StatsFragment]
 */
class StatsViewModel : ItemViewModel<StatsModel>() {
    val statsProperty = bind(StatsModel::stats) as ListProperty<Pair<String, String>>
    val isErrorProperty = bind(StatsModel::isError) as BooleanProperty

    fun getStats(): Disposable =
            StationsApi.service.getStats()
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
                        item = StatsModel(statsPair)
                        isErrorProperty.value = false
                    }, {
                        item = StatsModel()
                        isErrorProperty.value = true
                    })
}