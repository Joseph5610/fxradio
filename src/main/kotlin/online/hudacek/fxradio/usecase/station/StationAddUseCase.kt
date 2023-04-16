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

package online.hudacek.fxradio.usecase.station

import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.NewStationRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.NewStationResponse
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersSingle

private val logger = KotlinLogging.logger {}

private const val INVALID_UUID = "0"

/**
 * Adds new station to radio-browser API
 */
class StationAddUseCase : BaseUseCase<NewStationRequest, Single<NewStationResponse>>() {

    override fun execute(input: NewStationRequest): Single<NewStationResponse> = radioBrowserApi
        .addStation(input)
        .compose(applySchedulersSingle())
        .doOnSuccess { logger.debug { "New station added: $it " } }
        .onErrorResumeNext { Single.just(NewStationResponse(false, it.localizedMessage, INVALID_UUID)) }
}
