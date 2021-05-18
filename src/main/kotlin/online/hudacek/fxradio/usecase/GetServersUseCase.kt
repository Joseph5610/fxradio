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

package online.hudacek.fxradio.usecase

import javafx.collections.ObservableList
import javafx.concurrent.Task
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.http.HttpClient
import tornadofx.asObservable

/**
 * Retrieves valid list of radio-browser API urls
 */
class GetServersUseCase : BaseUseCase<Unit, Task<ObservableList<String>>>() {

    //API lookup URL
    private val lookupUrl = Config.API.dnsLookupURL

    override fun execute(input: Unit): Task<ObservableList<String>> = runAsync(daemon = true) {
        HttpClient.lookup(lookupUrl)
                .map { it.canonicalHostName }
                .distinct()
                .asObservable()
    }
}