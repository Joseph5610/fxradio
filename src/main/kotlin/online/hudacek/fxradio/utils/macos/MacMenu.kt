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

package online.hudacek.fxradio.utils.macos

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.SeparatorMenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.menu.separator
import tornadofx.FX

/**
 * NSMenu helpers
 */
object MacMenu {

    var isInTest = false

    //NSMenu toolkit
    private val menuToolkit by lazy { MenuToolkit.toolkit(FX.locale) }

    fun menuBar(op: MenuBar.() -> Menu) = MenuBar().apply {
        if (!isInTest) {
            useSystemMenuBarProperty().value = true
            menuToolkit.setApplicationMenu(op(this))
            menuToolkit.setMenuBar(this)
        }
    }

    fun appMenu(op: Menu.() -> Unit = {}) = Menu(FxRadio.appName).apply {
        if (!isInTest) {
            op(this)
            items.addAll(
                    separator(),
                    menuToolkit.createHideMenuItem(FxRadio.appName),
                    menuToolkit.createHideOthersMenuItem(),
                    menuToolkit.createUnhideAllMenuItem(),
                    separator(),
                    menuToolkit.createQuitMenuItem(FxRadio.appName))
        }
    }

    fun windowMenu(name: String) = Menu(name).apply {
        if (!isInTest) {
            menuToolkit.autoAddWindowMenuItems(this)
            items.addAll(
                    menuToolkit.createMinimizeMenuItem(),
                    menuToolkit.createZoomMenuItem(),
                    menuToolkit.createCycleWindowsItem(),
                    SeparatorMenuItem(),
                    menuToolkit.createBringAllToFrontItem())
        }
    }
}