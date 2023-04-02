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

package online.hudacek.fxradio.ui.menu

import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.ui.util.keyCombination
import online.hudacek.fxradio.viewmodel.AppMenuViewModel
import tornadofx.Controller
import tornadofx.get

abstract class BaseMenu(menuTitle: String) : Controller() {

    protected val appMenuViewModel: AppMenuViewModel by inject()
    protected val appEvent: AppEvent by inject()

    protected abstract val menuItems: List<MenuItem>

    /**
     * Parent menu object, extending classes defines its items via [menuItems] property
     */
    val menu by lazy {
        // Workaround to use actual key as menu text instead of
        // placeholder when key does not exist in Messages
        val actualTitle = if (messages[menuTitle].startsWith("[")) {
            menuTitle
        } else {
            messages[menuTitle]
        }

        menu(actualTitle) {
            items.addAll(menuItems)
        }
    }

    /**
     * Defines keyboard shortcuts for menu actions
     */
    protected companion object KeyCodes {
        val favouriteView = keyCombination(KeyCode.DIGIT1)
        val play = keyCombination(KeyCode.P)
        val stop = keyCombination(KeyCode.S)
        val newStation = keyCombination(KeyCode.N)
        val openLogs = keyCombination(KeyCode.O)
        val openWebsite = keyCombination(KeyCode.W)
        val openStream = keyCombination(KeyCode.U)
        val openPreferences = keyCombination(KeyCode.COMMA)
    }
}
