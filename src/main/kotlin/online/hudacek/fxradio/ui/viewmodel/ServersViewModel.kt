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
import io.reactivex.Observable
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.observableListOf
import tornadofx.property

enum class ServersViewState {
    Loading, Normal, Error
}

class ServersModel(selectedServer: String = Config.API.fallbackApiServerURL,
                   availableServers: ObservableList<String> = observableListOf(),
                   viewState: ServersViewState = ServersViewState.Normal) {
    var selected: String by property(selectedServer)
    var servers: ObservableList<String> by property(availableServers)
    var viewState: ServersViewState by property(viewState)
}

class ServersViewModel : ItemViewModel<ServersModel>(ServersModel()) {

    val savedServerValue = Property(Properties.API_SERVER)

    val serversProperty = bind(ServersModel::servers) as ListProperty<String>
    val selectedProperty = bind(ServersModel::selected) as StringProperty
    val viewStateProperty = bind(ServersModel::viewState) as ObjectProperty

    private val viewStateObservable: Observable<ServersViewState> = viewStateProperty
            .toObservableChangesNonNull()
            .map { it.newVal }

    /**
     * Perform async DNS lookup to find working API servers
     */
    init {
        viewStateObservable
                .filter { it == ServersViewState.Loading }
                .subscribe {
                    val servers = performLookup()
                    if (servers.isNullOrEmpty()) {
                        viewStateProperty.value = ServersViewState.Error
                    } else {
                        serversProperty.setAll(servers)
                        viewStateProperty.value = ServersViewState.Normal
                    }
                }
    }

    fun performLookup() = HttpClientHolder.client.lookup(Config.API.dnsLookupURL)
            .map { it.canonicalHostName }
            .distinct()
            .asObservable()

    //Save selected server to app.properties on commit
    override fun onCommit() = savedServerValue.save(selectedProperty.value)
}