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
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.observableListOf
import tornadofx.property
import java.net.InetAddress

class ServersModel(selectedServer: String, availableServers: ObservableList<String> = observableListOf()) {
    val selected: String by property(selectedServer)
    val servers: ObservableList<String> by property(availableServers)
}

class ServersViewModel : ItemViewModel<ServersModel>() {
    val serversProperty = bind(ServersModel::servers) as ListProperty<String>
    val selectedProperty = bind(ServersModel::selected) as StringProperty

    private val availableServers by lazy {
        InetAddress.getAllByName(Config.Resources.defaultDnsHost)
                .map { it.canonicalHostName }
                .distinct()
                .asObservable()
    }

    fun loadAllServers() {
        if (serversProperty.isEmpty()) {
            serversProperty.value = availableServers
        }
    }
}