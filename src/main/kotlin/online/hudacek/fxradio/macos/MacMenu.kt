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

package online.hudacek.fxradio.macos

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.SeparatorMenuItem
import online.hudacek.fxradio.FxRadio
import java.util.*

/**
 * NSMenu helpers
 */
object MacMenu {

    //NSMenu toolkit
    private val tk by lazy { MenuToolkit.toolkit(Locale.getDefault()) }

    fun menuBar(op: MenuBar.() -> Menu): MenuBar {
        return MenuBar().apply {
            useSystemMenuBarProperty().set(true)
            tk.setApplicationMenu(op.invoke(this))
            tk.setMenuBar(this)
        }
    }

    fun appMenu(op: Menu.() -> Unit = {}) = Menu(FxRadio.appName).apply {
        op.invoke(this)
        items.addAll(
                tk.createHideMenuItem(FxRadio.appName),
                tk.createHideOthersMenuItem(),
                tk.createUnhideAllMenuItem(),
                SeparatorMenuItem(),
                tk.createQuitMenuItem(FxRadio.appName))
    }

    fun windowMenu(name: String) = Menu(name).apply {
        tk.autoAddWindowMenuItems(this)
        items.addAll(
                tk.createMinimizeMenuItem(),
                tk.createZoomMenuItem(),
                tk.createCycleWindowsItem(),
                SeparatorMenuItem(),
                tk.createBringAllToFrontItem())
    }
}