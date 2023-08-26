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

package online.hudacek.fxradio.usecase.country

import io.reactivex.rxjava3.core.Observable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.isIgnoredCountry
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulers
import java.util.*


/**
 * Gets list of valid country names and count of stations in it
 */
class GetCountriesUseCase : BaseUseCase<Unit, Observable<Country>>() {

    override fun execute(input: Unit): Observable<Country> = radioBrowserApi
        .getCountries()
        .flattenAsObservable { it.filter { c -> !c.isIgnoredCountry } }
        .filter { it.stationCount != 0 }
        .map { it.copy(name = getCountryNameFromISO(it.iso3166) ?: it.name) }
        .sorted(Comparator.comparing(Country::name))
        .distinct()
        .compose(applySchedulers())

    private fun getCountryNameFromISO(iso3166: String?): String? {
        val countryCode: String? = isoCountries.firstOrNull { it == iso3166 }
        return if (countryCode != null) {
            Locale.of("", countryCode).displayName
        } else {
            null
        }
    }

    companion object {
        private val isoCountries = Locale.getISOCountries()
    }
}
