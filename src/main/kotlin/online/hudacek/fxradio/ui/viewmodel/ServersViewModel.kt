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
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import tornadofx.*
import java.net.InetAddress

enum class ServersViewState {
    Loading, Normal, Error
}

class ServersModel(selectedServer: String, availableServers: ObservableList<String> = observableListOf(), viewState: ServersViewState = ServersViewState.Loading) {
    val selected: String by property(selectedServer)
    val servers: ObservableList<String> by property(availableServers)
    val viewState: ServersViewState by property(viewState)
}

class ServersViewModel : ItemViewModel<ServersModel>() {
    val serversProperty = bind(ServersModel::servers) as ListProperty<String>
    val selectedProperty = bind(ServersModel::selected) as StringProperty
    val viewStateProperty = bind(ServersModel::viewState) as ObjectProperty

    /**
     * Perform async DNS lookup to find working API servers
     */
    fun loadAvailableServers(forceReload: Boolean = false) {
        if (serversProperty.isEmpty() || forceReload) {
            viewStateProperty.value = ServersViewState.Loading //set loading state of the fragment
            runAsync(daemon = true) {
                InetAddress.getAllByName(Config.API.dnsLookupURL)
                        .map { it.canonicalHostName }
                        .distinct()
                        .asObservable()
            } success {
                if (it.isNullOrEmpty()) {
                    viewStateProperty.value = ServersViewState.Error
                } else {
                    viewStateProperty.value = ServersViewState.Normal
                    serversProperty.value = it
                }
            } fail {
                viewStateProperty.value = ServersViewState.Error
            }
        }
    }
}