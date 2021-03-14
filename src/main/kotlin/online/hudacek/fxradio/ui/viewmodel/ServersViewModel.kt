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
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import tornadofx.*

enum class ServersViewState {
    Loading, Loaded, Error
}

class Servers(selectedServer: String = Config.API.fallbackApiServerURL,
              availableServers: ObservableList<String> = observableListOf(),
              viewState: ServersViewState = ServersViewState.Loaded) {
    var selected: String by property(selectedServer)
    var servers: ObservableList<String> by property(availableServers)
    var viewState: ServersViewState by property(viewState)
}

/**
 * Holds available and selected API servers
 * Item is set in [online.hudacek.fxradio.api.StationsApi.Companion]
 *
 * Search for available servers is performed only on first start of the app or when opening
 * [online.hudacek.fxradio.ui.modal.AvailableServersFragment]
 */
class ServersViewModel : ItemViewModel<Servers>(Servers()) {

    val savedServerValue = Property(Properties.API_SERVER)

    val serversProperty = bind(Servers::servers) as ListProperty<String>
    val selectedProperty = bind(Servers::selected) as StringProperty
    val viewStateProperty = bind(Servers::viewState) as ObjectProperty

    /**
     * Perform async DNS lookup to find working API servers
     */
    init {
        viewStateProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .filter { it == ServersViewState.Loading }
                .subscribe {
                    runAsync(daemon = true) {
                        performLookup()
                    } success {
                        if (it.isNullOrEmpty()) {
                            viewStateProperty.value = ServersViewState.Error
                        } else {
                            serversProperty.value = it
                            viewStateProperty.value = ServersViewState.Loaded
                        }
                    } fail {
                        viewStateProperty.value = ServersViewState.Error
                    }
                }
    }

    /**
     * Blocking operation is needed for the first start of the app
     */
    fun performLookup() = HttpClientHolder.client.lookup(Config.API.dnsLookupURL)
            .map { it.canonicalHostName }
            .distinct()
            .asObservable()

    //Save selected server to app.properties on commit
    override fun onCommit() = savedServerValue.save(selectedProperty.value)
}