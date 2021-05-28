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

package online.hudacek.fxradio.util

import javafx.stage.StageStyle
import online.hudacek.fxradio.ui.fragment.*
import tornadofx.Fragment
import tornadofx.find
import kotlin.reflect.full.createInstance

/**
 * Generic modal dialog helper
 * Type parameter should represent the fragment class loaded into the modal window
 */
sealed class Modal<out T : Fragment>(val style: StageStyle = StageStyle.UTILITY,
                                     val resizable: Boolean = false) {
    object StationInfo : Modal<StationInfoFragment>(style = StageStyle.UNDECORATED)
    object AddNewStation : Modal<AddStationFragment>()
    object About : Modal<AboutFragment>(resizable = true)
    object Servers : Modal<ServersFragment>(resizable = true)
    object Stats : Modal<StatsFragment>()
    object Attributions : Modal<AttributionsFragment>()
    object StationDebug : Modal<StationDebugFragment>()
    object License : Modal<AttributionsFragment.LicenseFragment>()
}

/**
 * Finds and opens modal window for [Modal]
 */
internal inline fun <reified T : Fragment> Modal<T>.open() = find<T>().openModal(
        stageStyle = style, resizable = resizable)

internal inline fun <reified T : Fragment> Modal<T>.new() = T::class.createInstance().openModal(
        stageStyle = style, resizable = resizable)
