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
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.stations.model.AddedStation
import online.hudacek.fxradio.apiclient.stations.model.StationBody
import online.hudacek.fxradio.util.applySchedulers

private val logger = KotlinLogging.logger {}

/**
 * Adds new station to radio-browser API
 */
class AddStationUseCase : BaseUseCase<StationBody, Single<AddedStation>>() {

    override fun execute(input: StationBody): Single<AddedStation> = stationsApi
            .addStation(input)
            .compose(applySchedulers())
            .onErrorResumeNext { Single.just(AddedStation(false, it.localizedMessage, "0")) }
            .doOnError { logger.error { "Error while adding station: ${it.message}" } }
}
