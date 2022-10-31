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

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.util.applySchedulers

/**
 * Retrieves valid list of radio-browser API urls
 */
class GetServersUseCase : BaseUseCase<Unit, Single<List<String>>>() {

    private val dnsResponse
        get() = HttpClient.lookup(Config.API.dnsLookupURL)

    override fun execute(input: Unit): Single<List<String>> = Observable.fromIterable(dnsResponse)
        .compose(applySchedulers())
        .map { it.canonicalHostName }
        .distinct()
        .toList()
}
