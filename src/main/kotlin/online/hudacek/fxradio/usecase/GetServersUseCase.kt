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

import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.viewmodel.ServersState
import tornadofx.asObservable
import tornadofx.fail
import tornadofx.success

/**
 * Retrieves valid list of radio-browser API urls
 */
class GetServersUseCase : BaseUseCase<ObjectProperty<ServersState>, Unit>() {

    private val lookupUrl = Config.API.dnsLookupURL

    override fun execute(input: ObjectProperty<ServersState>) {
        input.value = ServersState.Loading
        runAsync(daemon = true) {
            HttpClient.lookup(lookupUrl)
                    .map { it.canonicalHostName }
                    .distinct()
                    .asObservable()
        } success {
            if (it.isEmpty()) {
                input.value = ServersState.NoServersAvailable
            } else {
                input.value = ServersState.Fetched(it)
            }
        } fail {
            input.value = ServersState.Error(it.localizedMessage)
        }
    }
}
