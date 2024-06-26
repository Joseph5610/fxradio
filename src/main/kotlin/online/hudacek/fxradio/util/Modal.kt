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

import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import online.hudacek.fxradio.ui.fragment.AddStationFragment
import online.hudacek.fxradio.ui.fragment.AppInfoFragment
import online.hudacek.fxradio.ui.fragment.AttributionsFragment
import online.hudacek.fxradio.ui.fragment.CountriesSearchFragment
import online.hudacek.fxradio.ui.fragment.DebugFragment
import online.hudacek.fxradio.ui.fragment.OpenStreamFragment
import online.hudacek.fxradio.ui.fragment.PreferencesFragment
import online.hudacek.fxradio.ui.fragment.StatsFragment
import online.hudacek.fxradio.util.macos.MacUtils
import tornadofx.FX
import tornadofx.Fragment
import tornadofx.find
import tornadofx.findUIComponents

/**
 * Generic modal dialog helper
 * Type parameter should represent the fragment class loaded into the modal window
 */
sealed class Modal<out T : Fragment>(
    val style: StageStyle = StageStyle.DECORATED,
    val resizable: Boolean = false
) {
    data object AddNewStation : Modal<AddStationFragment>()
    data object AppInfo : Modal<AppInfoFragment>()
    data object Stats : Modal<StatsFragment>()
    data object Attributions : Modal<AttributionsFragment>()
    data object Debug : Modal<DebugFragment>()
    data object License : Modal<AttributionsFragment.LicenseFragment>()
    data object Preferences : Modal<PreferencesFragment>()
    data object OpenStream : Modal<OpenStreamFragment>()
    data object Countries : Modal<CountriesSearchFragment>()

    companion object {
        /**
         * Returns true if any [Fragment] is currently present in the scene
         */
        fun isAnyFragmentOpen() = FX.primaryStage.scene.findUIComponents().any { it is Fragment }
    }
}

/**
 * Finds and opens modal window for [Modal]
 */
internal inline fun <reified T : Fragment> Modal<T>.open() {
    // Ensure only one modal of given type is opened
    val stage = findOpenStage<T>()

    if (stage == null) {
        find<T>().openModal(stageStyle = style, resizable = resizable).also {
            it?.userData = T::class
            // We don't want stage icon in the modal dialog on macOS
            if (MacUtils.isMac) {
                it?.icons?.clear()
            }
        }
    } else {
        stage.requestFocus()
    }
}

/**
 * Finds and opens internal window for [Modal]
 */
internal inline fun <reified T : Fragment> Modal<T>.openInternalWindow() {
    val activeWindow = FX.primaryStage.scene.findUIComponents().firstOrNull { it is T }
    if (activeWindow == null) {
        // Only allow one internal window open
        find<T>().openInternalWindow<T>(
            owner = FX.primaryStage.scene.root, movable = resizable, closeButton = false
        )
    } else {
        // Closes the internal window if it is already opened
        activeWindow.close()
    }
}

/**
 * Finds and opens window for [Modal]
 */
internal inline fun <reified T : Fragment> Modal<T>.openWindow() {
    // Ensure only one window of given type is opened
    val stage = findOpenStage<T>()
    if (stage == null) {
        find<T>().openWindow(stageStyle = style, resizable = resizable).also {
            it?.userData = T::class
            // We don't want stage icon in window on macOS
            if (MacUtils.isMac) {
                it?.icons?.clear()
            }
        }
    } else {
        stage.requestFocus()
    }
}

/**
 * Returns first opened stage of type [T] or null if no such stage is opened
 */
private inline fun <reified T : Fragment> findOpenStage() = getAllStages().firstOrNull { it.userData == T::class }

/**
 * Returns list of currently opened Stages
 */
private fun getAllStages() = Window.getWindows().filterIsInstance<Stage>()