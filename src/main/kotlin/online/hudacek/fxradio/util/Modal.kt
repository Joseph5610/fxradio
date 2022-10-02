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

package online.hudacek.fxradio.util

import javafx.stage.StageStyle
import online.hudacek.fxradio.ui.fragment.AddStationFragment
import online.hudacek.fxradio.ui.fragment.AppInfoFragment
import online.hudacek.fxradio.ui.fragment.AttributionsFragment
import online.hudacek.fxradio.ui.fragment.DebugFragment
import online.hudacek.fxradio.ui.fragment.OpenStreamFragment
import online.hudacek.fxradio.ui.fragment.ServersFragment
import online.hudacek.fxradio.ui.fragment.StatsFragment
import tornadofx.FX
import tornadofx.Fragment
import tornadofx.find
import kotlin.reflect.full.createInstance

/**
 * Generic modal dialog helper
 * Type parameter should represent the fragment class loaded into the modal window
 */
sealed class Modal<out T : Fragment>(val style: StageStyle = StageStyle.UTILITY,
                                     val resizable: Boolean = false) {
    object AddNewStation : Modal<AddStationFragment>(style = StageStyle.UNIFIED)
    object AppInfo : Modal<AppInfoFragment>()
    object Servers : Modal<ServersFragment>(resizable = true)
    object Stats : Modal<StatsFragment>()
    object Attributions : Modal<AttributionsFragment>()
    object Debug : Modal<DebugFragment>()
    object License : Modal<AttributionsFragment.LicenseFragment>()
    object OpenStream: Modal<OpenStreamFragment>(style = StageStyle.UNIFIED)
}

/**
 * Finds and opens modal window for [Modal]
 */
internal inline fun <reified T : Fragment> Modal<T>.open() = find<T>().openModal(
        stageStyle = style, resizable = resizable)

internal inline fun <reified T : Fragment> Modal<T>.new() = T::class.createInstance().openModal(
        stageStyle = style, resizable = resizable)

internal inline fun <reified T : Fragment> Modal<T>.openInternalWindow() = find<T>().openInternalWindow<T>(
        owner = FX.primaryStage.scene.root, movable = resizable)
