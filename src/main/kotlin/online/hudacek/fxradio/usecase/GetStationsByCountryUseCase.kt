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

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.applySchedulersSingle

/**
 * Gets all stations from provided country name
 */
class GetStationsByCountryUseCase : BaseUseCase<Country, Single<List<Station>>>() {

    override fun execute(input: Country): Single<List<Station>> = radioBrowserApi
        .getStationsByCountryCode(countryCode = input.iso_3166_1)
        .compose(applySchedulersSingle())
        .flattenAsObservable { it }
        .map { it.copy(name = it.name.trim()) }
        .toList()
}
