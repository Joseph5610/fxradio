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
import javafx.scene.input.MouseEvent
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.menu.menu
import online.hudacek.fxradio.ui.menu.separator
import tornadofx.FX

/**
 * NSMenu helper for macOS only
 */
class NsMenu {

    /**
     * macOS default Application Menu
     */
    private val appMenu by lazy {
        menu(FxRadio.appName) {
            items.addAll(
                separator(),
                toolkit.createHideMenuItem(text),
                toolkit.createHideOthersMenuItem(),
                toolkit.createUnhideAllMenuItem(),
                separator(),
                toolkit.createQuitMenuItem(text)
            )
            toolkit.setApplicationMenu(this)
        }
    }

    private val menuBar by lazy {
        MenuBar().apply {
            useSystemMenuBarProperty().value = true
        }
    }

    inner class Builder {

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
                    toolkit.createMinimizeMenuItem(),
                    toolkit.createZoomMenuItem(),
                    toolkit.createCycleWindowsItem(),
                    separator(),
                    toolkit.createBringAllToFrontItem()
                )
            }
            toolkit.autoAddWindowMenuItems(menu)
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
    
    companion object {

        private val toolkit: MenuToolkit by lazy { MenuToolkit.toolkit(FX.locale) }

        fun showContextMenu(menu: Menu, e: MouseEvent) = toolkit.showContextMenu(menu, e)
    }
}
