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

import de.jangassen.MenuToolkit
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.menu.menu
import online.hudacek.fxradio.ui.menu.separator
import tornadofx.Component
import tornadofx.FX
import tornadofx.get

/**
 * NSMenu helper for macOS only
 */
class NSMenuHelper : Component() {

    private val tk by lazy { MenuToolkit.toolkit(FX.locale) }

    /**
     * macOS default Application Menu
     */
    private val appMenu by lazy {
        menu(FxRadio.appName) {
            items.addAll(
                separator(),
                tk.createHideMenuItem(text),
                tk.createHideOthersMenuItem(),
                tk.createUnhideAllMenuItem(),
                separator(),
                tk.createQuitMenuItem(text)
            )
            tk.setApplicationMenu(this)
        }
    }

    /**
     * Creates macOS style default Window menu
     */
    val windowMenu by lazy {
        menu(messages["macos.menu.window"]) {
            items.addAll(
                tk.createMinimizeMenuItem(),
                tk.createZoomMenuItem(),
                tk.createCycleWindowsItem(),
                separator(),
                tk.createBringAllToFrontItem()
            )
            tk.autoAddWindowMenuItems(this)
        }
    }

    val menuBar by lazy {
        MenuBar().apply {
            useSystemMenuBarProperty().value = true
        }
    }

    /**
     * Adds menus to the main MenuBar
     */
    fun addMenus(vararg menus: Menu) {
        menuBar.menus.addAll(menus)
    }

    /**
     * Adds additional MenuItems into the [appMenu]
     */
    fun addAppMenuItems(items: List<MenuItem>) {
        appMenu.items.addAll(0, items)
    }
}

