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

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.stations.model.CountriesBody
import online.hudacek.fxradio.apiclient.stations.model.Country
import online.hudacek.fxradio.apiclient.stations.model.isRussia
import online.hudacek.fxradio.util.applySchedulers

private val logger = KotlinLogging.logger {}

/**
 * Gets list of valid country names and count of stations in it
 */
class GetCountriesUseCase : BaseUseCase<ListProperty<Country>, Disposable>() {

    override fun execute(input: ListProperty<Country>): Disposable = apiService
            .getCountries(CountriesBody())
            .compose(applySchedulers())
            .flattenAsObservable { it }
            .filter { !it.isRussia }
            .subscribe({
                input.add(it)
            }, {
                logger.error(it) { "Exception while getting Countries!" }
            })
}