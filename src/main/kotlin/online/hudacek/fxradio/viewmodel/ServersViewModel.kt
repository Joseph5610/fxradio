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
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.usecase.GetServersUseCase
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import tornadofx.observableListOf
import tornadofx.property

sealed class ServersState(val key: String = "") {
    object Loading : ServersState("loading")
    data class Fetched(val servers: ObservableList<String>) : ServersState()
    object NoServersAvailable : ServersState("servers.notAvailable")
    data class Error(val cause: String) : ServersState("servers.error")
}

class Servers(
        selectedServer: String = Config.API.fallbackApiServerURL,
        availableServers: ObservableList<String> = observableListOf()
) {
    var selected: String by property(selectedServer)
    var servers: ObservableList<String> by property(availableServers)
}

/**
 * Holds available and selected API servers
 * Item is set in [online.hudacek.fxradio.api.StationsApiProvider]
 *
 * Search for available servers is performed only on first start of the app or when opening
 * [online.hudacek.fxradio.ui.fragment.ServersFragment]
 */
class ServersViewModel : BaseStateViewModel<Servers, ServersState>(Servers()) {

    private val getServersUseCase: GetServersUseCase by inject()

    val serversProperty = bind(Servers::servers) as ListProperty<String>
    val selectedProperty = bind(Servers::selected) as StringProperty

    /**
     * Perform async DNS lookup to find working API servers
     */
    fun fetchServers() = getServersUseCase.execute(stateProperty)

    override fun onNewState(newState: ServersState) {
        if (newState is ServersState.Fetched) {
            item = Servers(selectedServer = selectedProperty.value, availableServers = newState.servers)
        }
    }

    //Save selected server to app.properties on commit
    override fun onCommit() = Properties.ApiServer.save(selectedProperty.value)
}