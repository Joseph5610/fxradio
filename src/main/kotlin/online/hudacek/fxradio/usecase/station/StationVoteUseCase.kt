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

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.VoteResponse
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersSingle

/**
 * Increases vote count of the station
 */
class StationVoteUseCase : BaseUseCase<Station, Single<VoteResponse>>() {

    override fun execute(input: Station): Single<VoteResponse> = radioBrowserApi
            .addVote(input.uuid)
            .compose(applySchedulersSingle())
            .onErrorResumeNext {
                // We do not care if this response fails
                Single.just(VoteResponse(false, it.localizedMessage))
            }
}
