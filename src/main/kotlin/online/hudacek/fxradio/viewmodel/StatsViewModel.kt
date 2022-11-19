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
import online.hudacek.fxradio.apiclient.radiobrowser.model.StatsResponse
import online.hudacek.fxradio.usecase.GetStatsUseCase
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

sealed class StatsState {
    object Loading : StatsState()
    data class Fetched(val stats: StatsResponse) : StatsState()
    data class Error(val cause: String) : StatsState()
}

class Stats(statsList: ObservableList<String> = observableListOf()) {
    val statsList: ObservableList<String> by property(statsList)
}

/**
 * Holds information about radio-browser API stats and health
 * Shown inside [online.hudacek.fxradio.ui.fragment.StatsFragment]
 */
class StatsViewModel : BaseStateViewModel<Stats, StatsState>(Stats(), StatsState.Loading) {

    private val getStatsUseCase: GetStatsUseCase by inject()

    val statsListProperty = bind(Stats::statsList) as ListProperty

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
            item = Stats(newState.stats.convertToList())
        }
    }

    private fun StatsResponse.convertToList() = observableListOf(
        "${messages["stats.software_version"]}: $supportedVersion",
        "${messages["stats.status"]}: $status",
        "${messages["stats.stations"]}: $stations",
        "${messages["stats.stations_broken"]}: $stationsBroken",
        "${messages["stats.tags"]}: $tags",
        "${messages["stats.clicks_last_hour"]}: $clicksLastHour",
        "${messages["stats.clicks_last_day"]}: $clicksLastDay",
        "${messages["stats.languages"]}: $languages",
        "${messages["stats.countries"]}: $countries"
    )
}
