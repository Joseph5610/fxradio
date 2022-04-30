/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.usecase

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.stations.model.ClickResult
import online.hudacek.fxradio.apiclient.stations.model.Station

/**
 * Increases click count of the station
 */
class ClickUseCase : BaseUseCase<Station, Single<ClickResult>>() {

    override fun execute(input: Station): Single<ClickResult> = apiService
            .click(input.stationuuid)
            .compose(applySchedulers())
            .onErrorResumeNext {
                //We do not care if this response fails
                Single.just(ClickResult(false, it.localizedMessage, input.name))
            }
}