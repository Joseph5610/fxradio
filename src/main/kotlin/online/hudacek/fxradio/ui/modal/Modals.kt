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

package online.hudacek.fxradio.ui.modal

import javafx.stage.StageStyle
import tornadofx.Fragment
import tornadofx.find

/**
 * Generic modal dialog helper
 * Type parameter should represent the fragment class loaded into the modal window
 */
sealed class Modals<out T : Fragment>(val style: StageStyle = StageStyle.UTILITY,
                                      val resizable: Boolean = false) {
    object StationInfo : Modals<StationInfoFragment>()
    object AddNewStation : Modals<AddStationFragment>()
    object About : Modals<AboutFragment>(resizable = true)
    object Servers : Modals<ServersFragment>(resizable = true)
    object Stats : Modals<StatsFragment>()
    object Attributions : Modals<AttributionsFragment>()
    object StationDebug : Modals<StationDebugFragment>()
    object License : Modals<AttributionsFragment.LicenseFragment>()
}

/**
 * Finds and opens modal window for [Modals]
 */
internal inline fun <reified T : Fragment> Modals<T>.open() = find<T>().openModal(
        stageStyle = style,
        resizable = resizable)

