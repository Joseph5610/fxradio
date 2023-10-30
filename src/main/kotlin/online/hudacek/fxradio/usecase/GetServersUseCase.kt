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

package online.hudacek.fxradio.usecase

import javafx.application.Platform
import javafx.beans.property.Property
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.viewmodel.ServersState

/**
 * Retrieves valid list of radio-browser API servers
 */
class GetServersUseCase : BaseUseCase<Property<ServersState>, Unit>() {

    private val mainScope = MainScope()

    override fun execute(input: Property<ServersState>) {
        input.value = ServersState.Loading
        mainScope.launch {
            withContext(IO) {
                HttpClient.lookup(Config.API.DNS_LOOKUP_URL)
                    .map { it.canonicalHostName }
                    .distinct()
                    .let {
                        Platform.runLater {
                            input.value = if (it.isEmpty()) {
                                ServersState.NoServersAvailable
                            } else {
                                ServersState.Fetched(it)
                            }
                        }
                    }
            }
        }
    }
}
