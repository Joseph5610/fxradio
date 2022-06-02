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

import javafx.collections.ObservableList
import javafx.concurrent.Task
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import tornadofx.asObservable

/**
 * Retrieves valid list of radio-browser API urls
 */
class GetServersUseCase : BaseUseCase<Unit, Task<ObservableList<String>>>() {

    private val lookupUrl = Config.API.dnsLookupURL

    override fun execute(input: Unit): Task<ObservableList<String>> = runAsync(daemon = true) {
        HttpClient.lookup(lookupUrl)
                .map { it.canonicalHostName }
                .distinct()
                .asObservable()
    }
}