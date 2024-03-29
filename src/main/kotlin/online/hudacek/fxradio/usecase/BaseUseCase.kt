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

import io.reactivex.rxjava3.core.Single
import online.hudacek.fxradio.api.RadioBrowserApiProvider
import online.hudacek.fxradio.apiclient.radiobrowser.RadioBrowserApi
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.isIgnoredStation
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.util.applySchedulersSingle
import tornadofx.Controller

/**
 * UseCase interface defines actions for interaction with data layers
 */
abstract class BaseUseCase<InputType, OutputType> : Controller() {

    protected val appEvent: AppEvent by inject()

    protected val radioBrowserApi: RadioBrowserApi by lazy { RadioBrowserApiProvider.provide() }

    abstract fun execute(input: InputType): OutputType

    /**
     * Common method to filter stations from ignored countries
     */
    protected fun Single<List<Station>>.filterInvalidCountries() = flattenAsObservable { it }
        .filter { !it.isIgnoredStation }
        .map { it.copy(name = it.name.trim()) }
        .toList()
        .compose(applySchedulersSingle())
}
