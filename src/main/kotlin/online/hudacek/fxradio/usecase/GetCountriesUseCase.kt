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
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.CountriesBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.isRussia
import online.hudacek.fxradio.util.applySchedulersObservable

private val logger = KotlinLogging.logger {}

/**
 * Gets list of valid country names and count of stations in it
 */
class GetCountriesUseCase : BaseUseCase<CountriesBody, Observable<Country>>() {

    override fun execute(input: CountriesBody): Observable<Country> = radioBrowserApi
            .getCountries(input)
            .flattenAsObservable { it.filter { c -> !c.isRussia } }
            .compose(applySchedulersObservable())
            .doOnError { logger.error(it) { "Exception when downloading countries for $input" } }
}
