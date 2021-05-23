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

package online.hudacek.fxradio.util.macos

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.MenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.menu.menu
import online.hudacek.fxradio.ui.menu.separator
import tornadofx.FX

/**
 * NSMenu helper for MacOS only
 */
open class NSMenu {

    /**
     * NSMenu toolkit initialization
     */
    protected val menuToolkit: MenuToolkit by lazy { MenuToolkit.toolkit(FX.locale) }

    fun appMenu(menuItems: List<MenuItem>) = menu(FxRadio.appName) {
        if (!FxRadio.setTestEnvironment) {
            menuToolkit.setApplicationMenu(this)
            items.addAll(menuItems)
            items.addAll(
                    separator(),
                    menuToolkit.createHideMenuItem(FxRadio.appName),
                    menuToolkit.createHideOthersMenuItem(),
                    menuToolkit.createUnhideAllMenuItem(),
                    separator(),
                    menuToolkit.createQuitMenuItem(FxRadio.appName))
        }
    }

    fun windowMenu(name: String) = menu(name) {
        if (!FxRadio.setTestEnvironment) {
            menuToolkit.autoAddWindowMenuItems(this)
            items.addAll(
                    menuToolkit.createMinimizeMenuItem(),
                    menuToolkit.createZoomMenuItem(),
                    menuToolkit.createCycleWindowsItem(),
                    separator(),
                    menuToolkit.createBringAllToFrontItem())
        }
    }
}

