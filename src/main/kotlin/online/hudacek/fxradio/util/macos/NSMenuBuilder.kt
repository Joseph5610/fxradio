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
import tornadofx.FX

/**
 * NSMenu helper for macOS only
 */
class NSMenuBuilder {

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

    private val menuBar by lazy {
        MenuBar().apply {
            useSystemMenuBarProperty().value = true
        }
    }

    /**
     * Adds menus to the main MenuBar
     */
    fun addMenus(vararg menus: Menu) = apply {
        menuBar.menus.addAll(menus)
    }

    /**
     * Creates macOS style default Window menu
     */
    fun addWindowMenu(name: String) = apply {
        val menu = menu(name) {
            items.addAll(
                tk.createMinimizeMenuItem(),
                tk.createZoomMenuItem(),
                tk.createCycleWindowsItem(),
                separator(),
                tk.createBringAllToFrontItem()
            )
        }
        tk.autoAddWindowMenuItems(menu)
        menuBar.menus.add(menuBar.menus.size - 1, menu)
    }

    /**
     * Adds additional MenuItems into the [appMenu]
     */
    fun addAppMenuItems(items: List<MenuItem>) = apply {
        appMenu.items.addAll(0, items)
    }

    fun build() = menuBar
}
