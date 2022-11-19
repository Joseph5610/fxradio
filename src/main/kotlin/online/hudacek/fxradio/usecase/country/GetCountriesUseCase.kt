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

import io.reactivex.Observable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.isRussia
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersSingle
import java.util.*


/**
 * Gets list of valid country names and count of stations in it
 */
class GetCountriesUseCase : BaseUseCase<Unit, Observable<Country>>() {

    private val isoCountries = Locale.getISOCountries()

    override fun execute(input: Unit): Observable<Country> = radioBrowserApi
        .getCountries()
        .compose(applySchedulersSingle())
        .flattenAsObservable { it.filter { c -> !c.isRussia } }
        .filter { it.stationcount != 0 }
        .map { it.copy(name = getCountryNameFromISO(it.iso_3166_1) ?: it.name) }
        .sorted(Comparator.comparing(Country::name))
        .distinct()

    private fun getCountryNameFromISO(iso3166: String?): String? {
        val countryCode: String? = isoCountries.firstOrNull { it == iso3166 }
        return if (countryCode != null) {
            Locale("", countryCode).displayName
        } else {
            null
        }
    }

}
