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

package online.hudacek.fxradio.api

import online.hudacek.fxradio.apiclient.ApiServiceProvider
import online.hudacek.fxradio.apiclient.ApiUtils
import online.hudacek.fxradio.apiclient.stations.StationsApi
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.viewmodel.Servers
import online.hudacek.fxradio.viewmodel.ServersViewModel
import tornadofx.Component

object ApiClient : Component() {

    private val viewModel: ServersViewModel by inject()

    private val apiServerProperty = Property(Properties.ApiServer)

    val serviceProvider: ApiServiceProvider by lazy {
        if (apiServerProperty.isPresent) {
            viewModel.item = Servers(apiServerProperty.get())
        }
        ApiServiceProvider("https://${viewModel.selectedProperty.value}")
    }

    val service = serviceProvider.get<StationsApi>()

}