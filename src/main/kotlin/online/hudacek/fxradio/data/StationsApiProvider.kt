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

package online.hudacek.fxradio.data

import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.ApiServiceProvider
import online.hudacek.fxradio.apiclient.stations.StationsApi
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.viewmodel.Servers
import online.hudacek.fxradio.viewmodel.ServersViewModel
import tornadofx.Component

private val logger = KotlinLogging.logger {}

object StationsApiProvider : Component() {

    private val viewModel: ServersViewModel by inject()

    private val apiServerProperty by lazy { Property(Properties.ApiServer) }

    private val serviceProvider: ApiServiceProvider by lazy {
        logger.debug { "Initializing API provider" }
        if (apiServerProperty.isPresent) {
            viewModel.item = Servers(selectedServer = apiServerProperty.get())
        }
        ApiServiceProvider("https://${viewModel.selectedProperty.value}")
    }

    fun provide(): StationsApi = serviceProvider.get()

    fun close() = serviceProvider.close()
}
