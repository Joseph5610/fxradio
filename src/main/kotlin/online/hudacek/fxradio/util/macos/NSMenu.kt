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

package online.hudacek.fxradio.util.macos

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.MenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.menu.menu
import online.hudacek.fxradio.ui.menu.separator
import tornadofx.Component
import tornadofx.FX

/**
 * NSMenu helper for macOS only
 */
open class NSMenu : Component() {

    /**
     * NSMenu toolkit initialization
     */
    protected val menuToolkit: MenuToolkit by lazy { MenuToolkit.toolkit(FX.locale) }

    protected val isMenuEnabled by lazy { !(app as FxRadio).isAppRunningInTest }

    fun appMenu(menuItems: List<MenuItem>) = menu(FxRadio.appName) {
        if (isMenuEnabled) {
            menuToolkit.setApplicationMenu(this)
            items.addAll(menuItems)
            items.addAll(
                separator(),
                menuToolkit.createHideMenuItem(FxRadio.appName),
                menuToolkit.createHideOthersMenuItem(),
                menuToolkit.createUnhideAllMenuItem(),
                separator(),
                menuToolkit.createQuitMenuItem(FxRadio.appName)
            )
        }
    }

    fun windowMenu(name: String) = menu(name) {
        if (isMenuEnabled) {
            menuToolkit.autoAddWindowMenuItems(this)
            items.addAll(
                menuToolkit.createMinimizeMenuItem(),
                menuToolkit.createZoomMenuItem(),
                menuToolkit.createCycleWindowsItem(),
                separator(),
                menuToolkit.createBringAllToFrontItem()
            )
        }
    }
}

