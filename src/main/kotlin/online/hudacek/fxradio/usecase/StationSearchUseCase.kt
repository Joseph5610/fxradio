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
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchByTagBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.applySchedulers

/**
 * Searches for all stations that contains provided tag
 * if input.first is set to true, search is performed by tag, otherwise by name
 */
class StationSearchUseCase : BaseUseCase<Pair<Boolean, String>, Single<List<Station>>>() {

    override fun execute(input: Pair<Boolean, String>): Single<List<Station>> = if (input.first) {
        radioBrowserApi
                .searchStationByTag(SearchByTagBody(input.second))
                .compose(applySchedulers())
    } else {
        radioBrowserApi
                .searchStationByName(SearchBody(input.second))
                .compose(applySchedulers())
    }
}
